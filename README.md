## Scala BIP39

[![CircleCI](https://circleci.com/gh/AlanVerbner/bip39.svg?style=svg)](https://circleci.com/gh/AlanVerbner/bip39)
[![Coverage Status](https://coveralls.io/repos/github/AlanVerbner/bip39/badge.svg?branch=master)](https://coveralls.io/github/AlanVerbner/bip39?branch=master)

This is a toy BIP39 Scala based implementation I made for fun.

```scala
"com.github.alanverbner" %% "bip39" % "0.1"
```

### Example 

```scala
import com.github.alanverbner.bip39.{EnglishWordList, Entropy128, WordList}

val sentence = bip39.generate(Entropy128, WordList.load(EnglishWordList).get, new SecureRandom())
println(sentence)
bip39.check(sentence, EnglishWordList) shouldEqual true

```

### API

```scala
/**
    * Generates a BIP-39 mnemonic
    * @param entropy The size in bits of the entropy to be created
    * @param wordList Language based word list
    * @param secureRandom Cryptographically strong random number generator (RNG)
    * @return Mnemonic sentence based on random words joined by WordList delimiter
    */
  def generate(entropy: Entropy, wordList: WordList, secureRandom: SecureRandom): String
```

```scala
/***
    * Generates a BIP-39 mnemonic based on a pre-calculated entropy
    * @param ent Precalculated entropy. Size should be 16, 20, 24, 28 or 32.
    * @param wordList Language based word list
    * @return Mnemonic sentence based on random words joined by WordList delimiter
    */
  def generate(ent: Array[Byte], wordList: WordList): String
``` 

```scala
/**
    * Given a mnemonic and a given wordl list, determines if it's valid using BIP-39 checksum
    * @param mnemonic The mnemonic sentence used to derive seed bytes. Will be NFKD Normalized
    * @param wordList Language based word list
    * @return True if valid, false otherwise
    */
  def check(mnemonic: String, wordList: WordList): Boolean
```

```scala
  /**
    * Generates a seed bytes based on the given mnemonic and passphrase (if provided)
    * @param mnemonic The mnemonic sentence used to derive seed bytes. Will be NFKD Normalized
    * @param passphrase Optional passphrase used to protect seed bytes, defaults to empty
    * @return Seed bytes
    */
  def toSeed(mnemonic: String, passphrase: Option[String] = None): Array[Byte]
```

