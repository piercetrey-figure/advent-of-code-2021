import java.util.Collections

fun main() {
    fun List<String>.mostCommonDigits(): String = fold(Collections.nCopies(first().length, 0).toMutableList()) { acc, number ->
        number.toCharArray().mapIndexed { i, digit ->
            acc.set(i, acc[i] + (if (digit == '1') 1 else -1))
        }
        acc
    }.let {
        it.map {
            if (it >= 0) 1 else 0
        }.joinToString("")
    }
    fun String.invertDigits(): String = toCharArray().map { if (it == '0') '1' else '0' }.joinToString("")
    fun List<String>.leastCommonDigits(): String = mostCommonDigits().invertDigits()

    fun String.parseBinary() = toInt(2)

    fun part1(input: List<String>): Int {
        val gamma = input.mostCommonDigits()
        val epsilon = gamma.invertDigits()

        return gamma.parseBinary() * epsilon.parseBinary()
    }

    fun List<String>.getRating(keepMost: Boolean): String {
        var curr = this
        var position = 0
        while (true) {
            val mostCommon = if (keepMost) curr.mostCommonDigits() else curr.leastCommonDigits()
            val mostCommonDigit = mostCommon[position]
            curr = curr.filter { it[position] == mostCommonDigit }
            position++

            if (curr.size == 1) {
                return curr.first()
            }
        }
    }

    fun part2(input: List<String>): Int {
        val oxygenGeneratorRating = input.getRating(true)
        val c02ScrubberRating = input.getRating(false)
        return oxygenGeneratorRating.parseBinary() * c02ScrubberRating.parseBinary()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)

    val input = readInput("Day03")
    println(part1(input))

    check(part2(testInput) == 230)
    println(part2(input))
}
