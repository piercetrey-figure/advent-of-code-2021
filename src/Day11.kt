class Octopus(initialEnergy: Int) {
    var energyLevel = initialEnergy
        private set
    private var hasFlashed = false

    fun beginStep(): Boolean {
        energyLevel += 1
        if (hasFlashed) return false
        hasFlashed = energyLevel > 9
        return hasFlashed
    }

    fun endStep() {
        energyLevel = if (hasFlashed) 0 else energyLevel
        hasFlashed = false
    }
}

class Octopi(val rows: List<List<Octopus>>) {
    val width = rows.first().size
    val height = rows.size
    val size = width * height
    fun neighbors(x: Int, y: Int): List<Pair<Int, Int>> {
        return listOf(
            if (y > 0) x to y - 1 else null, // top
            if (x < width - 1) x + 1 to y else null, // right
            if (y < height - 1) x to y + 1 else null, // bottom
            if (x > 0) x - 1 to y else null, // left
            if (x > 0 && y > 0) x - 1 to y - 1 else null, // top left
            if (x < width - 1 && y > 0) x + 1 to y - 1 else null, // top right
            if (x < width - 1 && y < height - 1) x + 1 to y + 1 else null, // bottom right
            if (x > 0 && y < height - 1) x - 1 to y + 1 else null, // bottom left
        ).filterNotNull()
    }

    fun step(): Int {
        var numFlashed = 0
        for (y in 0..rows.size - 1) {
            for (x in 0..rows[y].size - 1) {
                numFlashed += step(x, y)
            }
        }
        rows.flatten().forEach { it.endStep() }
        return numFlashed
    }

    fun step(x: Int, y: Int): Int {
        val curr = rows[y][x]
        val flashed = curr.beginStep()
        if (flashed) {
            return 1 + neighbors(x, y).fold(0) { acc, (x2, y2) ->
                acc + step(x2, y2)
            }
        }
        return 0
    }
}

fun List<String>.toOctopi(): Octopi = map { it.split("").filterNot { it.isBlank() }.map { Octopus(it.toInt()) } }
    .let { Octopi(it) }

fun main() {
    fun part1(input: List<String>, numSteps: Int): Int {
        return input.toOctopi()
            .let { octopi ->
                (0 until numSteps).fold(0) { acc, curr ->
                    acc + octopi.step()
                }
            }
    }

    fun part2(input: List<String>): Int {
        return input.toOctopi().let { octopi ->
            var i = 1
            while (true) {
                if (octopi.step() == octopi.size) {
                    return@let i
                }
                i++
            }
            throw IllegalStateException("Failed to find synchronization point")
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput, 10) == 204)
    check(part1(testInput, 100) == 1656)

    val input = readInput("Day11")
    println(part1(input, 100))

    check(part2(testInput) == 195)
    println(part2(input))
}
