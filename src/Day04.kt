class Cell(val num: Int) {
    var marked = false
    fun mark() {
        marked = true
    }
}
data class Row(val columns: List<Cell>) {
    override fun toString(): String = columns.joinToString(" ")

    fun isWinner(): Boolean = columns.all {
        it.marked
    }

    fun markNumber(num: Number) {
        columns.filter { it.num == num }.forEach { it.mark() }
    }

    fun subScore() = columns.fold(0) { acc, curr -> acc + if (!curr.marked) curr.num else 0}

    fun markedString(): String = columns.map { if (it.marked) "x" else it.num.toString() }.joinToString(" ")
}
data class Board(val grid: List<Row>) {
    var lastNumberMarked: Int? = null
    override fun toString(): String = grid.joinToString("\n")

    fun toMarkedString(): String = grid.map { it.markedString() }.joinToString("\n")

    fun isWinner(): Boolean = hasWinningRow() || hasWinningColumn()

    fun hasWinningRow() = grid.any {
        it.isWinner()
    }

    fun hasWinningColumn(): Boolean {
        val width = grid.first().columns.size
        for (i in 0..width - 1) {
            if (grid.all { it.columns[i].marked }) {
                return true
            }
        }
        return false
    }

    fun markNumber(num: Int) {
        lastNumberMarked = num
        grid.forEach { it.markNumber(num) }
    }

    fun finalScore() = lastNumberMarked!! * unmarkedNumberSum()

    fun unmarkedNumberSum() = grid.fold(0) { acc, curr -> acc + curr.subScore() }
}
data class Game(val numbers: List<Int>, val boards: List<Board>) {
    fun play(): Board {
        for (num in numbers) {
            boards.forEach { it.markNumber(num) }
            boards.filter { it.isWinner() }
                .takeIf { it.isNotEmpty() }
                ?.let {
                    assert(it.size == 1) { "There were ${it.size} winners" }
                    println("Winning Board = ${it.first().toMarkedString()}")
                    return it.first()
                }
        }
        throw IllegalStateException("No winning boards found")
    }

    fun playUntilLastWins(): Board {
        var remainingBoards = boards
        for (num in numbers) {
            remainingBoards.forEach { it.markNumber(num) }
            remainingBoards = remainingBoards.filterNot { it.isWinner() }
                .apply {
                    if (isEmpty()) {
                        assert(remainingBoards.size == 1) { "There were ${remainingBoards.size} final winners" }
                        println("Last Winning Board = ${remainingBoards.first().toMarkedString()}")
                        return remainingBoards.first()
                    }
                }
        }
        throw IllegalStateException("Not all boards won")
    }
}

fun List<String>.asInts(): List<Int> = filter { !it.isEmpty() }.map { it.toInt() }
fun List<String>.asBoards(): List<Board> = map { it.split('\n').map { it.split(' ').asInts().map { Cell(it) }.let { Row(it) } }.filterNot { it.columns.isEmpty() }.let { Board(it) } }
fun List<String>.toGame(): Game = Game(first().split(',').asInts(), drop(1).joinToString("\n").split("\n\n").asBoards())

fun main() {
    fun part1(input: List<String>): Int {
        return input.toGame().play().finalScore()
    }

    fun part2(input: List<String>): Int {
        return input.toGame().playUntilLastWins().finalScore()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)

    val input = readInput("Day04")
    println(part1(input))

    check(part2(testInput) == 1924)
    println(part2(input))
}
