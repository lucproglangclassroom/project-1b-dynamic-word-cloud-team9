package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.apache.commons.collections4.queue.CircularFifoQueue
import scala.collection.mutable

class MainSpec extends AnyFlatSpec {

  "The word cloud generator" should "correctly count word frequencies" in {
    val words = Seq("hello", "world", "hello", "scala", "test", "hello", "world")
    val queue = new CircularFifoQueue[String]

    words.foreach(queue.add)

    val wordCount = mutable.Map[String, Int]()
    queue.forEach { w =>
      wordCount(w.nn) = wordCount.getOrElse(w.nn, 0) + 1
    }

    val sortedWords = wordCount.toSeq.sortBy { case (word, count) => (-count, word) }
    val topWords = sortedWords.take(3)

    topWords should contain theSameElementsAs Seq(("hello", 3), ("world", 2), ("scala", 1))
  }

  it should "ignore words shorter than the specified length" in {
    val words = Seq("hi", "there", "hello", "world")
    val queue = new CircularFifoQueue[String]

    words.foreach(queue.add)

    val wordCount = mutable.Map[String, Int]()
    queue.forEach { w =>
      if (w.nn.length >= 5) {
        wordCount(w.nn) = wordCount.getOrElse(w.nn, 0) + 1
      }
    }

    wordCount shouldBe Map("world" -> 1, "there" -> 1, "hello" -> 1)
  }

  it should "handle empty input gracefully" in {
    val queue = new CircularFifoQueue[String]
    val wordCount = mutable.Map[String, Int]()

    queue.forEach { w =>
      wordCount(w.nn) = wordCount.getOrElse(w.nn, 0) + 1
    }

    wordCount shouldBe empty
  }
}
