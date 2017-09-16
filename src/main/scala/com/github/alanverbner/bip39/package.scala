package com.github.alanverbner

import java.security.SecureRandom
import java.text.Normalizer.Form.NFKD
import java.text.Normalizer.normalize
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

import scodec.bits.BitVector
import scorex.crypto.hash.Sha256

package object bip39 {
  val WordListSize = 2048
  val BitsGroupSize = 11
  val Pbkdf2Algorithm = "PBKDF2WithHmacSHA512"
  val Pbkdf2Iterations = 2048
  val Pbkdf2KeyLength = 512
  val AllowedEntropyByteSizes = Seq(Entropy128, Entropy160, Entropy192, Entropy224, Entropy256).map(_.bits / 8)

  /**
    * Generates a BIP-39 mnemonic
    *
    * @param entropy      The size in bits of the entropy to be created
    * @param wordList     Language based word list
    * @param secureRandom Cryptographically strong random number generator (RNG)
    * @return Mnemonic sentence based on random words joined by WordList delimiter
    */
  def generate(entropy: Entropy, wordList: WordList, secureRandom: SecureRandom): String = {

    require(wordList.words.size == WordListSize, s"WordList should have $WordListSize words")

    val ent: Array[Byte] = BitVector.fill(entropy.bits)(high = false).toByteArray
    secureRandom.nextBytes(ent)

    generate(ent, wordList)

  }

  /** *
    * Generates a BIP-39 mnemonic based on a pre-calculated entropy
    *
    * @param ent      Precalculated entropy. Size should be 16, 20, 24, 28 or 32.
    * @param wordList Language based word list
    * @return Mnemonic sentence based on random words joined by WordList delimiter
    */
  def generate(ent: Array[Byte], wordList: WordList): String = {
    require(AllowedEntropyByteSizes contains ent.length, "Entropy size should be 32, 40, 48, 56 or 64")

    val checksum = BitVector(Sha256.hash(ent))
    val entWithChecksum = BitVector(ent) ++ checksum.take(ent.length / 4)

    entWithChecksum.grouped(BitsGroupSize).map { wordIndex =>
      wordList.words(wordIndex.toInt(signed = false))
    } mkString wordList.delimiter
  }

  /**
    * Given a mnemonic and a given wordl list, determines if it's valid using BIP-39 checksum
    *
    * @param mnemonic The mnemonic sentence used to derive seed bytes. Will be NFKD Normalized
    * @param wordList Language based word list
    * @return True if valid, false otherwise
    */
  def check(mnemonic: String, wordList: WordList): Boolean = {
    require(wordList.words.size == WordListSize, s"WordList should have $WordListSize words")

    val words = mnemonic.split(wordList.delimiter)
    if (words.length % 3 != 0) false
    else {
      val entWithChecksum = words
        .map(normalize(_, NFKD))
        .map(wordList.words.indexOf(_)).foldLeft(BitVector.empty) { (ent: BitVector, wordIndex: Int) =>
          ent ++ BitVector.fromInt(wordIndex, size = BitsGroupSize)
        }
      val checkSumSize = entWithChecksum.length / 33
      val ent = entWithChecksum.dropRight(checkSumSize)
      val checksum = entWithChecksum.takeRight(checkSumSize)
      checksum == BitVector(Sha256.hash(ent.toByteArray)).take(checkSumSize)
    }
  }

  /**
    * Generates a seed bytes based on the given mnemonic and passphrase (if provided)
    *
    * @param mnemonic   The mnemonic sentence used to derive seed bytes. Will be NFKD Normalized
    * @param passphrase Optional passphrase used to protect seed bytes, defaults to empty
    * @return Seed bytes
    */
  def toSeed(mnemonic: String, passphrase: Option[String] = None): Array[Byte] = {
    val normalizedMnemonic = normalize(mnemonic.toCharArray, NFKD).toCharArray
    val normalizedSeed = normalize(s"mnemonic${passphrase.getOrElse("")}", NFKD)

    val spec = new PBEKeySpec(
      normalizedMnemonic,
      normalizedSeed.getBytes,
      Pbkdf2Iterations,
      Pbkdf2KeyLength
    )
    val skf = SecretKeyFactory.getInstance(Pbkdf2Algorithm)
    skf.generateSecret(spec).getEncoded
  }

  sealed abstract class Entropy(val bits: Int)

  // scalastyle:off magic.number
  case object Entropy128 extends Entropy(128)

  case object Entropy160 extends Entropy(160)

  case object Entropy192 extends Entropy(192)

  case object Entropy224 extends Entropy(224)

  case object Entropy256 extends Entropy(256)
  // scalastyle:on <rule id>

}


