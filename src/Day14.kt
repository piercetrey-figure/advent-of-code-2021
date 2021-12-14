class PolymerizationTemplate(val initial: String, private val insertionRules: Map<Pair<Char, Char>, Char>) {
    private var pairs = hashMapOf<Pair<Char, Char>, Long>()

    init {
        initial.windowed(2).forEach { pairs[it.first() to it.last()] = (pairs[it.first() to it.last()] ?: 0) + 1 }
    }

    fun step() {
        val toAdd = hashMapOf<Pair<Char, Char>, Long>()
        pairs.forEach { (pair, count) ->
            val toInsert = insertionRules[pair]
            if (toInsert != null) {
                toAdd[pair.first to toInsert] = (toAdd[pair.first to toInsert] ?: 0) + count
                toAdd[toInsert to pair.second] = (toAdd[toInsert to pair.second] ?: 0) + count
            }
        }
        pairs = toAdd
    }

    fun mostMinusLeast(): Long = pairs.flatMap { listOf(it.key.first to it.value, it.key.second to it.value) }.fold(hashMapOf<Char, Long>()) { acc, curr ->
        acc[curr.first] = (acc[curr.first] ?: 0) + curr.second
        acc
    }.map {
        it.key to it.value / 2 + if (initial.first() == it.key || initial.last() == it.key) 1 else 0
    }.toMap().let {
        it.values.maxOrNull()!! - it.values.minOrNull()!!
    }
}

fun List<String>.toInitialAndPolymerizationTemplate(): PolymerizationTemplate = drop(2).map { it.split(" -> ").let { it.first().let { it.first() to it.last() } to it.last().first() } }.toMap().let { PolymerizationTemplate(first(), it) }

fun PolymerizationTemplate.polymerize(numSteps: Int): PolymerizationTemplate = apply {
    (0..numSteps - 1).forEach {
        step()
    }
}

fun String.mostMinusLeast(): Long = groupBy { it }.let { charLists ->
    charLists.maxOf { it.value.size }.toLong() - charLists.minOf { it.value.size }.toLong()
}

fun main() {
    fun part1(input: List<String>): Long {
        return input.toInitialAndPolymerizationTemplate().polymerize(10).mostMinusLeast()
    }

    fun part2(input: List<String>): Long {
        return input.toInitialAndPolymerizationTemplate().polymerize(40).mostMinusLeast()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588L)

    val input = readInput("Day14")
    println(part1(input))

    check(part2(testInput) == 2188189693529)
    println(part2(input))
}
