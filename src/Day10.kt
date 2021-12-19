import kotlin.coroutines.EmptyCoroutineContext.fold

val chunkOptions = listOf(
    "(" to ")",
    "[" to "]",
    "{" to "}",
    "<" to ">",
).toMap()

val illegalPoints = listOf(
    ")" to 3,
    "]" to 57,
    "}" to 1197,
    ">" to 25137,
).toMap()

val completionPoints = listOf(
    ")" to 1,
    "]" to 2,
    "}" to 3,
    ">" to 4,
).toMap()

fun main() {
    fun part1(input: List<String>): Int {
        return input.fold(0) { points, line ->
            val stack = mutableListOf<String>()
            var score = 0
            for (char in line.split("").filterNot { it.isBlank() }) {
                if (chunkOptions.containsKey(char)) { // opening tag
                    stack.add(char)
                } else { // closing tag
                    val corresponding = stack.removeLast()
                    if (chunkOptions[corresponding] != char) {
                        score = illegalPoints[char]!!
                        break
                    }
                }
            }
            points + score
        }
    }

    fun part2(input: List<String>): Long {
        return input.fold(0 to mutableListOf<Long>()) { (illegal, completion), line ->
            val stack = mutableListOf<String>()
            var illegalScore = 0
            for (char in line.split("").filterNot { it.isBlank() }) {
                if (chunkOptions.containsKey(char)) { // opening tag
                    stack.add(char)
                } else { // closing tag
                    val corresponding = stack.removeLast()
                    if (chunkOptions[corresponding] != char) {
                        illegalScore = illegalPoints[char]!!
                        break
                    }
                }
            }
            if (illegalScore == 0) { // complete line
                completion.add(stack.reversed().fold(0) { acc, curr ->
                    (acc * 5) + completionPoints[chunkOptions[curr]!!]!!
                })
            }
            (illegal + illegalScore) to completion
        }.second.also { println("before is $it") }.sorted().also { println("sorted is $it") } .let { it[it.size / 2] }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)

    val input = readInput("Day10")
    println(part1(input))

    check(part2(testInput) == 288957L)
    println(part2(input))
}
