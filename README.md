# Playfair Cipher

A Java implementation of the **Playfair cipher**, the classic digraph substitution cipher used in the Boer War and World War I.

**▶ Try it in your browser and watch each step on the key square:** <https://d3r8nll5h7sqin.cloudfront.net/projects/playfair/index.html>

This started as a cryptography course project at Dalhousie University. This repo is a cleaned-up Java version, and the interactive web version on my portfolio uses the same algorithm.

## The algorithm

1. **Key square.** Write the key's letters without repeats, then the rest of the alphabet, into a 5×5 grid. I and J share a cell.
2. **Pairs.** Split the message into two-letter pairs. If a pair would repeat a letter (`LL`), insert an `X`. If one letter is left over at the end, pad it with `X`.
3. **Transform each pair:**
   - **Same row:** take the letter to the right of each one (to the left when decrypting).
   - **Same column:** take the letter below each one (above when decrypting).
   - **Rectangle:** swap each letter for the corner in its own row on the other side.

## Run it

You need Java 11 or newer.

```bash
javac -d out src/*.java
java -cp out PlayfairCipher
```

```
Secret key: playfair example

Key square:
 P   L   A   Y   F
I/J  R   E   X   M
 B   C   D   G   H
 K   N   O   Q   S
 T   U   V   W   Z

(e)ncrypt, (d)ecrypt or (q)uit? e
Text: Hide the gold in the tree stump
Ciphertext: BMODZBXDNABEKUDMUIXMMOUVIF
```

## Use it from code

```java
PlayfairCipher cipher = new PlayfairCipher("playfair example");
String secret = cipher.encrypt("Hide the gold in the tree stump"); // BMODZBXDNABEKUDMUIXMMOUVIF
String plain  = cipher.decrypt(secret);                            // HIDETHEGOLDINTHETREXESTUMP
char[][] square = PlayfairCipher.generateKeyMatrix("playfair example");
```

## Tests

```bash
javac -d out src/*.java test/*.java && java -cp out PlayfairCipherTest
```

The tests check against the well-known textbook examples (the `playfair example` and `MONARCHY` keys).

> The Playfair cipher is a fun piece of history, but it's **not secure** by modern standards. Don't use it to protect real secrets.
