open class RiskGrid(val rows: List<List<Int>>) {
    val width = rows.first().size
    val height = rows.size
    val start = Point(0, 0)
    val destination = Point(width - 1, height - 1)

    val Point.risk: Int
        get() = if (this == start) 0 else rows[y][x]

    val Point.neighbors: List<Point>
        get() = listOf(
            if (y < height - 1) Point(x, y + 1) else null, // bottom
            if (x < width - 1) Point(x + 1, y) else null, // right
            if (x > 0) Point(x - 1, y) else null, // left
            if (y > 0) Point(x, y - 1) else null, // top
        ).filterNotNull()

    fun findLowestRisk(): Int {
        var curr = start
        val unvisited = hashSetOf<Point>()
        val distances = rows.flatMapIndexed { y, row -> row.mapIndexed { x, _ -> Point(x, y) to Int.MAX_VALUE } }.toMap().toMutableMap()
        distances[start] = 0
        val visited = mutableSetOf<Point>()
        while (curr != destination) {
            visited.add(curr)
            curr.neighbors
                .filterNot { visited.contains(it) }
                .forEach {
                    distances[it] = Math.min(distances[curr]!! + it.risk, distances[it] ?: Int.MAX_VALUE)
                    unvisited.add(it)
                }
            curr = unvisited.minByOrNull { distances[it]!! }!!.also {
                unvisited.remove(it)
            }

            if (visited.contains(destination)) {
                break
            }
        }
        return distances[destination]!!
    }

    override fun toString(): String = rows.joinToString("\n") {
        it.joinToString("")
    }
}

fun List<List<Int>>.multiplyX(): List<List<Int>> = map { row ->
    row + (1 until 5).fold(row to listOf<Int>()) { (prev, all), add2 -> prev.map { r -> ((r + 1) % 10).coerceAtLeast(1) }.let { it to (all + it) }  }.second
}

fun List<List<Int>>.multiplyY(): List<List<Int>> = this + (1 until 5).fold(this to listOf<List<Int>>()) { (prev, all), add ->
    prev.mapIndexed { y, row ->
        row.mapIndexed { x, risk ->
            ((risk + 1) % 10).coerceAtLeast(1)
        }
    }.let { it to (all + it)}
}.second

fun List<String>.toIntGrid() = map { it.split("").filterNot { it.isBlank() }.map { it.toInt() } }

fun main() {
    fun part1(input: List<String>): Int {
        return RiskGrid(input.toIntGrid()).findLowestRisk()
    }

    fun part2(input: List<String>): Int {
        return RiskGrid(input.toIntGrid().multiplyX().multiplyY()).findLowestRisk()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)

    val input = readInput("Day15")
    println(part1(input))

    check(part2(testInput) == 315)
    println(part2(input))
}
