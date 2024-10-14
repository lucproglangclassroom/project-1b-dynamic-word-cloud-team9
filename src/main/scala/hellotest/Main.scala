package hellotest
import org.apache.commons.collections4.queue.CircularFifoQueue
import scala.collection.mutable
import scala.language.unsafeNulls
import scala.io.Source

// ArgumentParser trait handles argument validation and parsing
trait ArgumentParser:
  val DEFAULT_CLOUD_SIZE = 10
  val DEFAULT_LENGTH_AT_LEAST = 6
  val DEFAULT_WINDOW_SIZE = 1000
  val DEFAULT_UPDATE_STEPS = 1
  val DEFAULT_MIN_FREQUENCY = 1

  def parseArguments(args: Array[String]): (Int, Int, Int, Int, Int) =
    var cloud_size = DEFAULT_CLOUD_SIZE
    var length_at_least = DEFAULT_LENGTH_AT_LEAST
    var window_size = DEFAULT_WINDOW_SIZE
    var update_steps = DEFAULT_UPDATE_STEPS
    var min_frequency = DEFAULT_MIN_FREQUENCY

    try
      if args.length >= 1 then cloud_size = args(0).toInt
      if args.length >= 2 then length_at_least = args(1).toInt
      if args.length >= 3 then window_size = args(2).toInt
      if args.length >= 4 then update_steps = args(3).toInt
      if args.length == 5 then min_frequency = args(4).toInt

      if cloud_size < 1 || length_at_least < 1 || window_size < 1 || update_steps < 1 || min_frequency < 1 then
        throw NumberFormatException()
    catch
      case _: NumberFormatException =>
        Console.err.println("The arguments should be natural numbers")
        System.exit(4)

    (cloud_size, length_at_least, window_size, update_steps, min_frequency)

// InputProcessor trait handles splitting lines into words and applying filters
trait InputProcessor:
  def processInput(lines: Iterator[String], lengthAtLeast: Int): Iterator[String] =
    lines
      .flatMap(_.split("(?U)[^\\p{Alpha}0-9']+"))
      .map(_.toLowerCase)
      .filter(_.length >= lengthAtLeast)

// QueueManager trait handles the circular buffer (FIFO queue) logic
trait QueueManager:
  def manageQueue(queue: CircularFifoQueue[String], word: String): Unit =
    queue.add(word) // Adds the word to the circular buffer

  def isQueueFull(queue: CircularFifoQueue[String], windowSize: Int): Boolean =
    queue.size == windowSize

// WordCloudGenerator trait handles word frequency calculation and printing
trait WordCloudGenerator:
  def generateWordCloud(queue: CircularFifoQueue[String], cloudSize: Int, minFrequency: Int): Unit =
    val wordCount = mutable.Map[String, Int]()
    queue.forEach { w =>
      wordCount(w) = wordCount.getOrElse(w, 0) + 1
    }

    val filteredWords = wordCount.filter { case (_, count) => count >= minFrequency }
    val sortedWords = filteredWords.toSeq.sortBy { case (word, count) => (-count, word) }
    val topWords = sortedWords.take(cloudSize)

    println(topWords.map { case (word, count) => s"$word: $count" }.mkString(" "))

// Main object that combines the traits
object Main extends ArgumentParser, InputProcessor, QueueManager, WordCloudGenerator:
  def main(args: Array[String]): Unit =
    if args.length > 5 then
      Console.err.println("usage: ./target/universal/stage/bin/main [cloud-size] [length-at-least] [window-size] [update-steps] [min-frequency]")
      System.exit(2)

    // Parse arguments
    val (cloudSize, lengthAtLeast, windowSize, updateSteps, minFrequency) = parseArguments(args)

    // Input processing
    val lines = scala.io.Source.stdin.getLines
    val words = processInput(lines, lengthAtLeast)

    // Queue management
    val queue = CircularFifoQueue[String](windowSize)

    // Process words and generate word cloud
    var stepCount = 0
    words.foreach { word =>
      manageQueue(queue, word)
      stepCount += 1

      if isQueueFull(queue, windowSize) && stepCount % updateSteps == 0 then
        generateWordCloud(queue, cloudSize, minFrequency)

        // Here you can implement dynamic graphical visualization
        // (e.g., by calling a method to update the UI)
    }

end Main