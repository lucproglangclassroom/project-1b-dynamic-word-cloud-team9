package hellotest

import scala.io.Source
import scala.annotation.tailrec

object Main:

  // Default values for arguments
  val CLOUD_SIZE = 10
  val LENGTH_AT_LEAST = 6
  val WINDOW_SIZE = 1000

  def main(args: Array[String]): Unit =

    // Parse command-line arguments or use defaults
    val (cloudSize, lengthAtLeast, windowSize) = parseArgs(args)

    // Read lines from stdin, split into words, and filter by length
    val words = Source.stdin.getLines.flatMap(tokenize).filter(_.length >= lengthAtLeast)

    // Process words in sliding windows and generate word clouds
    processWords(words, windowSize, cloudSize)

  // Parse command-line arguments with default fallbacks
  def parseArgs(args: Array[String]): (Int, Int, Int) =
    if args.length > 3 then
      Console.err.println("usage: ./target/universal/stage/bin/main [cloud-size] [length-at-least] [window-size]")
      sys.exit(2)
    
    val cloudSize = if args.length >= 1 then args(0).toIntOption.getOrElse(CLOUD_SIZE) else CLOUD_SIZE
    val lengthAtLeast = if args.length >= 2 then args(1).toIntOption.getOrElse(LENGTH_AT_LEAST) else LENGTH_AT_LEAST
    val windowSize = if args.length == 3 then args(2).toIntOption.getOrElse(WINDOW_SIZE) else WINDOW_SIZE

    (cloudSize, lengthAtLeast, windowSize)

  // Tokenize the line into words using regex, only keeping alphanumeric characters and apostrophes
  def tokenize(line: String): List[String] =
    @tailrec
    def tokenizeAcc(remaining: List[Char], currentWord: List[Char], acc: List[String]): List[String] =
      remaining match
        case Nil if currentWord.isEmpty => acc
        case Nil => currentWord.reverse.mkString :: acc
        case c :: cs if c.isLetterOrDigit || c == '\'' =>
          tokenizeAcc(cs, c.toLower :: currentWord, acc)
        case _ :: cs if currentWord.isEmpty =>
          tokenizeAcc(cs, List(), acc)
        case _ :: cs =>
          tokenizeAcc(cs, List(), currentWord.reverse.mkString :: acc)

    tokenizeAcc(line.toList, List(), List()).reverse

    
  // Process the words by sliding a window and printing word clouds
  @tailrec
  def processWords(words: Iterator[String], windowSize: Int, cloudSize: Int, window: List[String] = List.empty): Unit =
    if words.hasNext then
      val nextWord = words.next()
      val updatedWindow = (nextWord :: window).take(windowSize)
      
      if updatedWindow.size == windowSize then
        val wordCloud = generateWordCloud(updatedWindow, cloudSize)
        printWordCloud(wordCloud)
      
      processWords(words, windowSize, cloudSize, updatedWindow)
    else
      ()

  // Generate a word cloud (sorted by frequency and alphabetically) for the given window of words
  def generateWordCloud(window: List[String], cloudSize: Int): List[(String, Int)] =
    window
      .groupBy(identity)
      .view.mapValues(_.size)
      .toList
      .sortBy { case (word, count) => (-count, word) }
      .take(cloudSize)

  // Print the word cloud in the desired format
  def printWordCloud(wordCloud: List[(String, Int)]): Unit =
    println(wordCloud.map { case (word, count) => s"$word: $count" }.mkString(" "))

end Main
