import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

/**
 * Split a string by commas and map to ints
 */
fun String.toInts() = split(",").map { it.toInt() }

data class Point(val x: Int, val y: Int) {
    override fun toString() = "$x,$y"
}