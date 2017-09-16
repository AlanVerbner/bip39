package com.github.alanverbner.bip39

import java.text.Normalizer

import scala.io.{BufferedSource, Codec, Source}
import scala.util.Try

object WordList {

  def load(language: WordListLanguage): Try[WordList] = language match {
    case ChineseSimplifiedWordList => loadFile(fileLoader("chinese_simplified.txt")).map(WordList(_, " "))
    case ChineseTraditionalWordList => loadFile(fileLoader("chinese_traditional.txt")).map(WordList(_, " "))
    case EnglishWordList => loadFile(fileLoader("english.txt")).map(WordList(_, " "))
    case FrenchWordList => loadFile(fileLoader("french.txt")).map(WordList(_, " "))
    case ItalianWordList => loadFile(fileLoader("italian.txt")).map(WordList(_, " "))
    case JapaneseWordList => loadFile(fileLoader("japanese.txt")).map(WordList(_, "\u3000"))
    case KoreanWordList => loadFile(fileLoader("korean.txt")).map(WordList(_, " "))
    case SpanishList => loadFile(fileLoader("spanish.txt")).map(WordList(_, " "))
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

  private def fileLoader(fileName: String): () => BufferedSource =
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

