package com.github.alanverbner.bip39

import com.github.alanverbner.bip39

import scala.io.{BufferedSource, Codec, Source}
import scala.util.Try

object WordList {

  def load(language: WordListLanguage): Try[WordList] = {
    val maybeWordList = language match {
      case ChineseSimplifiedWordList => loadFile(resourceLoader("chinese_simplified.txt")).map(WordList(_, " "))
      case ChineseTraditionalWordList => loadFile(resourceLoader("chinese_traditional.txt")).map(WordList(_, " "))
      case EnglishWordList => loadFile(resourceLoader("english.txt")).map(WordList(_, " "))
      case FrenchWordList => loadFile(resourceLoader("french.txt")).map(WordList(_, " "))
      case ItalianWordList => loadFile(resourceLoader("italian.txt")).map(WordList(_, " "))
      case JapaneseWordList => loadFile(resourceLoader("japanese.txt")).map(WordList(_, "\u3000"))
      case KoreanWordList => loadFile(resourceLoader("korean.txt")).map(WordList(_, " "))
      case SpanishList => loadFile(resourceLoader("spanish.txt")).map(WordList(_, " "))
      case CustomWordList(path, delimiter) => loadFile(() => Source.fromFile(path, "UTF-8")).map(WordList(_, delimiter))
    }

    maybeWordList.filter(_.words.size == bip39.WordListSize)
  }

  private def loadFile(loadFile: () => BufferedSource): Try[Seq[String]] = {
    Try(loadFile()).map { r =>
      try {
        r.getLines().toList
      } finally {
        r.close()
      }
    }
  }

  private def resourceLoader(fileName: String): () => BufferedSource =
    () => Source.fromResource(s"wordlists/$fileName")(Codec.UTF8)
}

case class WordList(words: Seq[String], delimiter: String)

sealed trait WordListLanguage

case object ChineseSimplifiedWordList extends WordListLanguage
case object ChineseTraditionalWordList extends WordListLanguage
case object EnglishWordList extends WordListLanguage
case object FrenchWordList extends WordListLanguage
case object ItalianWordList extends WordListLanguage
case object JapaneseWordList extends WordListLanguage
case object KoreanWordList extends WordListLanguage
case object SpanishList extends WordListLanguage
case class CustomWordList(path: String, delimiter: String) extends WordListLanguage

