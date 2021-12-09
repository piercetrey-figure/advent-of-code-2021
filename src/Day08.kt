fun String.sort(): String = toCharArray().sorted().joinToString("")
fun CharArray.overlapsAll(other: CharArray) = other.all { segment -> this.any { segment == it } }

class Display(val patterns: List<String>, val outputPatterns: List<String>) {
    val allPatterns = (patterns + outputPatterns).map { it.sort() }.toSet()
    .map { it.toCharArray() }

    private val numberMapping = mutableMapOf<Int, CharArray>()

    init {
        numberMapping[1] = allPatterns.first { it.size == 2 }
        numberMapping[4] = allPatterns.first { it.size == 4 }
        numberMapping[7] = allPatterns.first { it.size == 3 }
        numberMapping[8] = allPatterns.first { it.size == 7 }
        numberMapping[3] = allPatterns.first { it.size == 5 && it.overlapsAll(numberMapping[7]!!) }
        numberMapping[9] = allPatterns.first { it.size == 6 && it.overlapsAll(numberMapping[4]!!) }
        numberMapping[6] = allPatterns.first { it.size == 6 && !it.overlapsAll(numberMapping[1]!!) }
        numberMapping[0] = allPatterns.first { it.size == 6 && !it.contentEquals(numberMapping[6]!!) && !it.contentEquals(numberMapping[9]!!) }
        numberMapping[5] = allPatterns.first { it.size == 5 && numberMapping[6]!!.overlapsAll(it) }
        numberMapping[2] = allPatterns.first { it.size == 5 && !it.contentEquals(numberMapping[3]!!) && !it.contentEquals(numberMapping[5]!!) }
    }

    private val reverseNumberMapping = numberMapping.map {
        it.value.joinToString("") to it.key
    }.toMap()

    val numUnique: Int = outputPatterns.filter { listOf(1, 4, 7, 8).contains(reverseNumberMapping[it.sort()]!!) }.size

    val outputValue = outputPatterns.map {
        reverseNumberMapping[it.sort()]!!
    }.joinToString("").toInt()
}
fun String.toDisplay(): Display = split(" | ").let { (patterns, outputPatterns) -> Display(patterns.split(" "), outputPatterns.split(" ")) }
fun List<String>.toDisplays(): List<Display> = map { it.toDisplay() }

fun main() {
    fun part1(input: List<String>): Int {
        return input.toDisplays().fold(0) { acc, display ->
            acc + display.numUnique
        }
    }

    fun part2(input: List<String>): Int {
        return input.toDisplays().fold(0) { acc, display ->
            acc + display.outputValue
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)

    val input = readInput("Day08")
    println(part1(input))

    check(part2(listOf("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf")) == 5353)

    check(part2(testInput) == 61229)
    println(part2(input))
}
