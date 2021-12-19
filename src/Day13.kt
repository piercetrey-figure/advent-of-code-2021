import java.lang.IllegalStateException

data class Point(val x: Int, val y: Int)
sealed class Fold(val line: Int)
class X(line: Int): Fold(line)
class Y(line: Int): Fold(line)
data class TransparentGrid(val coordinates: List<Point>, val width: Int = coordinates.sortedBy { it.x }.last().x, val height: Int = coordinates.sortedBy { it.y }.last().y) {
    val numPoints = coordinates.size
    fun foldX(foldLine: Int): TransparentGrid {
        val newWidth = (width - 1) / 2
        return (0..height).mapNotNull { y ->
            (0..newWidth).mapNotNull { x ->
                if (coordinates.contains(Point(x, y)) || coordinates.contains(Point(foldLine + (foldLine - x), y))) {
                    Point(x, y)
                } else {
                    null
                }
            }
        }.flatten().let {
            TransparentGrid(it, newWidth, height)
        }
    }

    fun foldY(foldLine: Int): TransparentGrid {
        val newHeight = (height - 1) / 2
        return (0..newHeight).mapNotNull { y ->
            (0..width).mapNotNull { x ->
                if (coordinates.contains(Point(x, y)) || coordinates.contains(Point(x, foldLine + (foldLine - y)))) {
                    Point(x, y)
                } else {
                    null
                }
            }
        }.flatten().let {
            TransparentGrid(it, width, newHeight)
        }
    }

    fun fold(fold: Fold): TransparentGrid = when(fold) {
        is X -> foldX(fold.line)
        is Y -> foldY(fold.line)
    }

    override fun toString(): String = (0..height).joinToString("\n") { y ->
        (0..width).joinToString("") { x ->
            if (coordinates.contains(Point(x, y))) "#" else "."
        }
    }
}

fun List<String>.toGridAndFolds(): Pair<TransparentGrid, List<Fold>> = filterNot { it.isBlank() }.partition { !it.contains("fold along") }.let { it.first.toTransparentGrid() to it.second.toFolds() }
fun List<String>.toTransparentGrid(): TransparentGrid = map { it.toInts().let { (x, y) -> Point(x, y) } }.let { TransparentGrid(it) }
fun List<String>.toFolds(): List<Fold> = map { it.substringAfter("fold along ").split("=").let { (type, amount) ->
    when (type) {
        "x" -> X(amount.toInt())
        "y" -> Y(amount.toInt())
        else -> throw IllegalStateException("$type=$amount not a valid fold")
    }
} }

fun main() {
    fun part1(input: List<String>): Int {
        return input.toGridAndFolds().let { (grid, folds) ->
            grid.fold(folds.first())
        }.numPoints
    }

    fun part2(input: List<String>): TransparentGrid {
        return input.toGridAndFolds().let { (grid, folds) ->
            folds.fold(grid) { acc, f -> acc.fold(f) }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)

    val input = readInput("Day13")
    println(part1(input))

//    check(part2(testInput) == 0)
//    println(part2(testInput))
    println(part2(input))
}
