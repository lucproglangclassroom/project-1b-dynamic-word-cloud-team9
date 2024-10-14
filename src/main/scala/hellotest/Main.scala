package hellotest

import org.apache.commons.collections4.queue.CircularFifoQueue
import scala.collection.mutable
import scala.io.Source

object Main:

  // Default values for arguments
  val CLOUD_SIZE = 10
  val LENGTH_AT_LEAST = 6
  val WINDOW_SIZE = 1000
  val UPDATE_STEPS = 1
  val MIN_FREQUENCY = 1

  def main(args: Array[String]) = 

    // Argument validity checking
    if (args.length > 5) {
      System.err.nn.println("usage: ./target/universal/stage/bin/main [cloud-size] [length-at-least] [window-size] [update-steps] [min-frequency]")
      System.exit(2)
    }

    // Parse the command-line arguments or use the default values
    var cloud_size = CLOUD_SIZE
    var length_at_least = LENGTH_AT_LEAST
    var window_size = WINDOW_SIZE
    var update_steps = UPDATE_STEPS
    var min_frequency = MIN_FREQUENCY

    try {
      if (args.length >= 1) cloud_size = args(0).toInt
      if (args.length >= 2) length_at_least = args(1).toInt
      if (args.length >= 3) window_size = args(2).toInt
      if (args.length >= 4) update_steps = args(3).toInt
      if (args.length == 5) min_frequency = args(4).toInt

      if (cloud_size < 1 || length_at_least < 1 || window_size < 1 || update_steps < 1 || min_frequency < 1)
        throw new NumberFormatException()
    } catch {
      case _: NumberFormatException =>
        Console.err.println("The arguments should be natural numbers")
        System.exit(4)
    }

    // Set up input from stdin and process words
    val lines = scala.io.Source.stdin.getLines
    val words = lines.flatMap(l => l.nn.split("(?U)[^\\p{Alpha}0-9']+").nn).nn.map(_.nn.toLowerCase)

    // Circular buffer to hold the most recent words
    val queue = new CircularFifoQueue[String](window_size)

    // Process words and update word cloud
    var stepCount = 0
    words.filter(_.nn.length >= length_at_least).foreach { word =>
      queue.add(word) // Add word to the queue
      stepCount += 1

      // Only start processing once the queue is filled with `window_size` words
      if (queue.size == window_size && stepCount % update_steps == 0) {
        // Compute word frequencies
        val wordCount = mutable.Map[String, Int]()
        queue.forEach { w =>
          wordCount(w.nn) = wordCount.getOrElse(w.nn, 0) + 1
        }

        // Filter by minimum frequency
        val filteredWords = wordCount.filter { case (_, count) => count >= min_frequency }

        // Sort by frequency (descending) and alphabetically in case of ties
        val sortedWords = filteredWords.toSeq.sortBy { case (word, count) => (-count, word) }

        // Take the top `cloud_size` words
        val topWords = sortedWords.take(cloud_size)

        // Print the word cloud in the required format
        println(topWords.map { case (word, count) => s"$word: $count" }.mkString(" "))

        // Here you can implement dynamic graphical visualization
        // (e.g., by calling a method to update the UI)
      }
    }

end Main
