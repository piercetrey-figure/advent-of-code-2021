data class Cave(val name: String) {
    val isStart = name == "start"
    val isEnd = name == "end"
    val isSmall = name.toCharArray().all { it.isLowerCase() } && !isStart && !isEnd

    override fun toString(): String = name
}

class CaveGraph(val startCaves: List<Cave>, val caveConnections: Map<Cave, List<Cave>>, val endCaves: List<Cave>) {
    fun getDistinctPaths(maxSmallVisits: Int = 1): List<List<Cave>> {
        val visited = mutableSetOf<List<Cave>>()
        return startCaves.mapNotNull { followPath(it, listOf(), visited, maxSmallVisits) }.flatten()
    }

    private fun followPath(start: Cave, trace: List<Cave>, visited: MutableSet<List<Cave>>, maxSmallVisits: Int): List<List<Cave>>? {
        val newTrace = trace + start
        if (visited.contains(newTrace)) {
            return null
        }

        visited.add(newTrace)

        val willGoOverThreshold = newTrace.filter { it.isSmall }.groupBy { it }.run { any { it.value.size > maxSmallVisits } || count { it.value.size == maxSmallVisits && maxSmallVisits > 1 } > 1 }
        if (willGoOverThreshold) {
            return null
        }

        if (start.isStart && trace.isNotEmpty()) {
            return null
        }

        if (start.isEnd) {
            return listOf(newTrace)
        }

        return caveConnections.get(start)?.mapNotNull { cave ->
            followPath(cave, newTrace, visited, maxSmallVisits)
        }?.flatten()
    }

    override fun toString(): String {
        return "startCaves = $startCaves\ncaveConnections = $caveConnections\nendCaves = $endCaves"
    }
}

fun List<String>.toGraph(): CaveGraph {
    val connections = map { it.split('-').let { (c1, c2) -> Cave(c1) to Cave(c2) } }
        .run {
            groupBy { it.first }.mapValues { it.value.map { it.second }.toMutableList() }.toMutableMap().let { forward ->
                groupBy { it.second }.mapValues { it.value.map { it.first } }.let { reverse ->
                    reverse.onEach { entry ->
                        if (forward.containsKey(entry.key)) {
                            forward.get(entry.key)!!.addAll(entry.value)
                        } else {
                            forward.set(entry.key, entry.value.toMutableList())
                        }
                    }
                    forward.mapValues { it.value.toList().sortedBy { it.name } }.toMap()
                }
            }
        }
    val start = connections.map { it.key }.filter { it.isStart }
    val end = connections.map { it.key }.filter { it.isEnd }
    return CaveGraph(start, connections, end)
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.toGraph().getDistinctPaths().size
    }

    fun part2(input: List<String>): Int {
        return input.toGraph().getDistinctPaths(2).size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val testInput2 = readInput("Day12_test2")
    val testInput3 = readInput("Day12_test3")
    check(part1(testInput) == 10)
    check(part1(testInput2) == 19)
    check(part1(testInput3) == 226)

    val input = readInput("Day12")
    println(part1(input))

    check(part2(testInput) == 36)
    check(part2(testInput2) == 103)
    check(part2(testInput3) == 3509)
    println(part2(input))
}
