fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day<day>_test")
    check(part1(testInput) == 0)

    val input = readInput("Day<day>")
    println(part1(input))

    check(part2(testInput) == 0)
    println(part2(input))
}
