import kotlin.coroutines.EmptyCoroutineContext.fold

class Grid(val rows: List<List<Int>>) {
    val width = rows.first().size
    val height = rows.size

    private fun getNeighbors(x: Int, y: Int): List<Int> = getNeighborCoordinates(x, y).map { (x, y) ->
        rows[y][x]
    }

    private fun getNeighborCoordinates(x: Int, y: Int): List<Pair<Int, Int>> = listOf(
        if (y > 0) x to y - 1 else null, // top
        if (x < width - 1) x + 1 to y else null, // right
        if (y < height - 1) x to y + 1 else null, // bottom
        if (x > 0) x - 1 to y else null, // left
    ).filterNotNull()

    val lowSpots: List<Int> = rows.flatMapIndexed { y, row ->
        row.filterIndexed { x, cell -> getNeighbors(x, y).all { neighbor -> neighbor > cell } }
    }

    val basins: List<List<Pair<Int, Int>>>
        get() {
            val visited = mutableListOf<Pair<Int, Int>>()
            return rows.flatMapIndexed { y, row ->
                row.mapIndexedNotNull { x, cell ->
                    getBasin(x, y, visited)
                }
            }
        }

    private fun getBasin(x: Int, y: Int, visited: MutableList<Pair<Int, Int>>): List<Pair<Int, Int>>? {
        if (visited.contains(x to y)) return null
        visited.add(x to y)

        if (rows[y][x] == 9) return null

        return getNeighborCoordinates(x, y).mapNotNull { (x2, y2) ->
            getBasin(x2, y2, visited)
        }.flatten() + (x to y)
    }
}

fun List<String>.toGrid() = map { it.split("").filterNot { it.isBlank() }.map { it.toInt() } }.let { Grid(it) }

fun main() {
    fun part1(input: List<String>): Int {
        return input.toGrid().lowSpots.fold(0) { acc, curr -> acc + curr + 1 }
    }

    fun part2(input: List<String>): Int {
        return input.toGrid().basins
            .sortedByDescending { it.size }
            .take(3)
            .fold(1) { acc, curr -> acc * curr.size }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)

    val input = readInput("Day09")
    println(part1(input))

    check(part2(testInput) == 1134)
    println(part2(input))
}
