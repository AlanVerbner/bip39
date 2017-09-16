package com.github.alanverbner.bip39

import org.scalatest.{FlatSpec, Matchers}

class WordListSuite extends FlatSpec with Matchers {

  "WordList" should "load all default word lists" in {
    Seq(ChineseSimplifiedWordList,
      ChineseTraditionalWordList,
      EnglishWordList,
      FrenchWordList,
      ItalianWordList,
      JapaneseWordList,
      KoreanWordList,
      SpanishList
    ).foreach(wl => {
      WordList.load(wl).isSuccess shouldBe true
    })
  }

  it should "fail if a non existing word list is being loaded" in {
    WordList.load(CustomWordList("fake.txt", "")).isFailure shouldBe true
  }

  it should "load a custom word list" in {
    val delimiter = "|"
    val custom = WordList.load(CustomWordList(getClass.getResource("/custom_wordlist.txt").getPath, delimiter))
    custom.isSuccess shouldBe true
    custom.get.delimiter shouldBe delimiter
    custom.get.words.foreach(w => w == "bip39")
  }

  it should "fail loading custom word list with less than 2048 words" in {
    val delimiter = "|"
    val custom = WordList.load(CustomWordList(getClass.getResource("/incomplete_wordlist.txt").getPath, delimiter))
    custom.isFailure shouldBe true
  }
}
