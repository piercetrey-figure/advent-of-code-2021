data class LineSegment(val p1: Point, val p2: Point) {
    val isHorizontal = p1.y == p2.y
    val isVertical = p1.x == p2.x

    val containedPoints = calculateContainedPoints()

    fun calculateContainedPoints(): List<Point> {
        val points = mutableListOf<Point>(p1)
        val (deltaX, deltaY) = p1 delta p2
        do {
            points.add(Point(points.last().x + deltaX, points.last().y + deltaY))
        } while (points.last() != p2);
        return points.toList()
    }
}
class Field(val segments: List<LineSegment>) {
    val positions = hashMapOf<Point, Int>()
    var maxX = 0
    var maxY = 0

    fun markSegment(segment: LineSegment) = segment.containedPoints.forEach {
        maxX = Math.max(it.x, maxX)
        maxY = Math.max(it.y, maxY)
        positions[it] = (positions[it] ?: 0) + 1
    }

    fun markHorizontalVertical() = apply {
        segments.filter {
            it.isHorizontal || it.isVertical
        }.forEach(::markSegment)
    }

    fun markAll() = apply {
        segments.forEach(::markSegment)
    }

    fun getNumIntersectionsGreaterThan(numIntersections: Int): Int = positions.filter {
        it.value > numIntersections
    }.size

    override fun toString(): String {
        var str = ""
        for (x in 1..maxX) {
            for (y in 1..maxY) {
                str += positions[Point(x, y)] ?: "."
            }
            str += "\n"
        }
        return str
    }
}

fun String.toPoint(): Point = split(",").map { it.toInt() }.let { (x, y) -> Point(x, y) }
fun String.toLineSegment(): LineSegment = split(" -> ").let { (p1, p2) -> LineSegment(p1.toPoint(), p2.toPoint()) }
infix fun Point.delta(other: Point): Pair<Int, Int> = when {
    x > other.x -> -1
    x < other.x -> 1
    else -> 0
} to when {
    y > other.y -> -1
    y < other.y -> 1
    else -> 0
}

fun List<String>.toField(): Field = Field(map { it.toLineSegment() })

fun main() {
    fun part1(input: List<String>): Int {
        return input.toField().markHorizontalVertical().getNumIntersectionsGreaterThan(1)
    }

    fun part2(input: List<String>): Int {
        return input.toField().markAll().getNumIntersectionsGreaterThan(1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)

    val input = readInput("Day05")
    println(part1(input))

    check(part2(testInput) == 12)
    println(part2(input))
}
