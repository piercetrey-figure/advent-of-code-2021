import java.util.Collections

fun String.toInts() = split(",").map { it.toInt() }

fun List<Int>.advance(days: Int): Long {
    var dayBuckets = Collections.nCopies(9, 0L).toMutableList()
    forEach { dayBuckets[it] = dayBuckets[it] + 1 }
    for (i in 1..days) {
        val numReproducing = dayBuckets.first()
        dayBuckets = dayBuckets.drop(1).plus(0).toMutableList()
        dayBuckets[8] = numReproducing
        dayBuckets[6] += numReproducing
    }
    return dayBuckets.sum()
}

fun main() {
    fun part1(input: List<String>): Long {
        return input.first().toInts().advance(80)
    }

    fun part2(input: List<String>): Long {
        return input.first().toInts().advance(256)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934L)

    val input = readInput("Day06")
    println(part1(input))

    check(part2(testInput) == 26984457539L)
    println(part2(input))
}
