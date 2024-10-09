package hellotest
import org.apache.commons.collections4.queue.CircularFifoQueue
import scala.collection.mutable
import scala.language.unsafeNulls
import scala.io.Source

// the ArgumentParser trait handles argument validation
trait ArgumentParser {
  val DEFAULT_CLOUD_SIZE = 10
  val DEFAULT_LENGTH_AT_LEAST = 6
  val DEFAULT_WINDOW_SIZE = 1000

  def parseArguments(args: Array[String]): (Int, Int, Int) = {
    var cloud_size = DEFAULT_CLOUD_SIZE
    var length_at_least = DEFAULT_LENGTH_AT_LEAST
    var window_size = DEFAULT_WINDOW_SIZE

    try {
      if (args.length >= 1) {
        cloud_size = args(0).toInt
        if (cloud_size < 1) throw new NumberFormatException()
      }
      if (args.length >= 2) {
        length_at_least = args(1).toInt
        if (length_at_least < 1) throw new NumberFormatException()
      }
      if (args.length == 3) {
        window_size = args(2).toInt
        if (window_size < 1) throw new NumberFormatException()
      }
    } catch {
      case _: NumberFormatException =>
        Console.err.println("The arguments should be natural numbers")
        System.exit(4)
    }

    (cloud_size, length_at_least, window_size)
  }
}

// the InputProcessor trait handles splitting lines into words and applying filters
trait InputProcessor {
  def processInput(lines: Iterator[String], lengthAtLeast: Int): Iterator[String] = {
    lines
      .flatMap(_.split("(?U)[^\\p{Alpha}0-9']+"))
      .map(_.toLowerCase)
      .filter(_.length >= lengthAtLeast)
  }
}

// the QueueManager trait handles the circular buffer (FIFO queue) logic
trait QueueManager {
  def manageQueue(queue: CircularFifoQueue[String], word: String): Unit = {
    queue.add(word) // Adds the word to the circular buffer
  }

  def isQueueFull(queue: CircularFifoQueue[String], windowSize: Int): Boolean = {
    queue.size == windowSize
  }
}

// the WordCloudGenerator trait handles word frequency calculation and printing
trait WordCloudGenerator {
  def generateWordCloud(queue: CircularFifoQueue[String], cloudSize: Int): Unit = {
    val wordCount = mutable.Map[String, Int]()
    queue.forEach { w =>
      wordCount(w) = wordCount.getOrElse(w, 0) + 1
    }

    val sortedWords = wordCount.toSeq.sortBy { case (word, count) => (-count, word) }
    val topWords = sortedWords.take(cloudSize)

    println(topWords.map { case (word, count) => s"$word: $count" }.mkString(" "))
  }
}

// Main object that combines the traits
object Main extends ArgumentParser with InputProcessor with QueueManager with WordCloudGenerator {
  def main(args: Array[String]): Unit = {
    if (args.length > 3) {
      Console.err.println("usage: ./target/universal/stage/bin/main [cloud-size] [length-at-least] [window-size]")
      System.exit(2)
    }

    // parses arguments
    val (cloudSize, lengthAtLeast, windowSize) = parseArguments(args)

    // Input processing
    val lines = scala.io.Source.stdin.getLines
    val words = processInput(lines, lengthAtLeast)

    // the queue management
    val queue = new CircularFifoQueue[String](windowSize)

    // processes words and generate word cloud
    words.foreach { word =>
      manageQueue(queue, word)
      if (isQueueFull(queue, windowSize)) {
        generateWordCloud(queue, cloudSize)
      }
    }
  }
}
