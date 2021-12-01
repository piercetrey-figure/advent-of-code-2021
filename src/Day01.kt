fun main() {
    data class DepthTracker(val previous: Int, val numIncreased: Int)
    fun List<Int>.getDepthIncreases() = fold(DepthTracker(first(), 0)) { acc, curr ->
        DepthTracker(curr, acc.numIncreased + if (curr > acc.previous) 1 else 0)
    }.numIncreased

    fun part1(input: List<String>): Int {
        val depths = input.map { it.toInt() }
        return depths.getDepthIncreases()
    }

    fun part2(input: List<String>): Int {
        val depths = input.map { it.toInt() }
        return depths.windowed(3, 1, false) {
            it.sum()
        }.getDepthIncreases()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 7)

    val input = readInput("Day01")
    println(part1(input))

    check(part2(testInput) == 5)
    println(part2(input))
}
