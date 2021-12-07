import kotlin.coroutines.EmptyCoroutineContext.fold
import kotlin.math.abs

fun List<Int>.calculateMinFuel(fuelCalculator: (Int) -> Int = { it }): Int {
    var minFuel: Int? = null
    var minPosition: Int? = null
    for (i in minOrNull()!!..maxOrNull()!!) {
        val fuelUsed = fold(0) { acc, pos -> acc + fuelCalculator(abs(pos - i)) }
        if (minFuel == null || fuelUsed < minFuel) {
            minFuel = fuelUsed
            minPosition = i
        }
    }
    return minFuel!!
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.first().toInts().calculateMinFuel()
    }

    fun part2(input: List<String>): Int {
        val exponentialCache = mutableMapOf<Int, Int>()
        return input.first().toInts().calculateMinFuel { exponentialCache.getOrElse(it) {
            var res = 0
            for (i in 1..it) {
                res += i
            }
            res
        } }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)

    val input = readInput("Day07")
    println(part1(input))

    check(part2(testInput) == 168)
    println(part2(input))
}
