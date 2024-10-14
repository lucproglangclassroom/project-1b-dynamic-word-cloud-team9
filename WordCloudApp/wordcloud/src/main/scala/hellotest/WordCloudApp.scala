import org.apache.commons.collections4.queue.CircularFifoQueue
import scala.collection.mutable
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.VBox
import scalafx.scene.text.{Font, Text}
import scalafx.scene.paint.Color

object WordCloudApp extends JFXApp3 {

  // Default parameters
  val CLOUD_SIZE = 10
  val LENGTH_AT_LEAST = 6
  val WINDOW_SIZE = 1000
  val UPDATE_STEPS = 1
  val MIN_FREQUENCY = 1

  // Create a VBox to hold the word cloud
  val wordCloudContainer = new VBox()

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title.value = "Dynamic Word Cloud"
      scene = new Scene(wordCloudContainer, 800, 600)
    }

    // Add initial content to the scene
    val initialText = new Text("Welcome to the Word Cloud App!") {
      font = new Font(20)
      fill = Color.Black
    }
    wordCloudContainer.children.add(initialText)
  }

  def updateWordCloud(words: Seq[String]): Unit = {
    println("Updating word cloud with words: " + words.mkString(", "))
    wordCloudContainer.children.clear() // Clear previous words

    val queue = new CircularFifoQueue[String]
    val wordCount = mutable.Map[String, Int]()

    words.foreach { word =>
      queue.add(word)
      if (queue.size == WINDOW_SIZE) {
        // Compute word frequencies
        queue.forEach { w =>
          wordCount(w) = wordCount.getOrElse(w, 0) + 1
        }

        // Filter and sort the words
        val filteredWords = wordCount.filter { case (_, count) => count >= MIN_FREQUENCY }
        val sortedWords = filteredWords.toSeq.sortBy { case (word, count) => (-count, word) }
        val topWords = sortedWords.take(CLOUD_SIZE)

        // Create visual elements for each word
        topWords.foreach { case (word, count) =>
          val size = Math.max(10, count * 10) // Example: scale font size by frequency
          val text = new Text(word) {
            font = new Font(size)
            fill = Color.Black
          }
          wordCloudContainer.children.add(text)
          println(s"Added word: $word with font size $size")
        }
      }
    }
  }

  // Simulate reading from stdin and updating the word cloud
  new Thread(() => {
    // Simulated input words
    val simulatedWords = Seq("hello", "world", "hello", "scala", "test", "hello", "world")
    
    // Simulate the streaming input
    simulatedWords.foreach { word =>
      Thread.sleep(500) // Simulate time between words
      updateWordCloud(simulatedWords.take(7)) // Update with all words (or a dynamic source)
    }
  }).start()
}
