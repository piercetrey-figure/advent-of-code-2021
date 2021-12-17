import kotlin.math.sign

class Probe(val targetArea: Pair<Point, Point>, initialVelocity: Point) {
    var position = Point(0, 0)
        private set
    var velocity = initialVelocity // I know, I know, not really a point, but a convenient x/y pair
        private set

    fun step() {
        position = Point(position.x + velocity.x, position.y + velocity.y)
        velocity = Point(velocity.x - velocity.x.sign, velocity.y - 1)
    }

    val inTargetArea: Boolean
        get() = position.x <= Math.max(targetArea.first.x, targetArea.second.x) && position.x >= Math.min(targetArea.first.x, targetArea.second.x) &&
                position.y <= Math.max(targetArea.first.y, targetArea.second.y) && position.y >= Math.min(targetArea.first.y, targetArea.second.y)

    val missedTarget: Boolean
        get() = overshotX || undershotX || overshotY

    val undershotX: Boolean
        get() = position.x < Math.min(targetArea.first.x, targetArea.second.x) && position.y < Math.min(targetArea.first.y, targetArea.second.y)
    val overshotX: Boolean
        get() = position.x > Math.max(targetArea.first.x, targetArea.second.x)
    val overshotY: Boolean
        get() = position.y < Math.min(targetArea.first.y, targetArea.second.y)
}

fun String.toValues(): Pair<Int, Int> = split("=").drop(1).first().split("..").let { (v1, v2) -> v1.toInt() to v2.toInt() }
fun String.toTargetArea(): Pair<Point, Point> = removePrefix("target area: ").split(", ").let { (xComponent, yComponent) -> xComponent.toValues() to yComponent.toValues() }.let { (x, y) -> Point(x.first, y.first) to Point(x.second, y.second) }

fun main() {
    fun part1(input: List<String>): Int {
        return input.first().toTargetArea().let { targetArea ->
            var maxY = Int.MIN_VALUE
            var initialVelocity = Point(99, 0)
            while (initialVelocity.x > 0 && initialVelocity.x < 100) {
                do {
                    initialVelocity = Point(initialVelocity.x, initialVelocity.y + 1)
                    val probe = Probe(targetArea, initialVelocity)
                    var tempMaxY = probe.position.y
                    while (!probe.inTargetArea && !probe.missedTarget) {
                        probe.step()
                        tempMaxY = Math.max(tempMaxY, probe.position.y)
                    }
                    if (probe.missedTarget) {
                    } else {
                        maxY = Math.max(maxY, tempMaxY)
                    }
                } while (!probe.overshotX && !probe.undershotX && initialVelocity.y < 100)
                initialVelocity = Point(initialVelocity.x - 1, 0)
            }
            maxY
        }
    }

    fun part2(input: List<String>): Int {
        return input.first().toTargetArea().let { targetArea ->
            var initialVelocity = Point(10000, 10000)
            var successCount = 0
            do {
                val probe = Probe(targetArea, initialVelocity)
                while (!probe.inTargetArea && !probe.missedTarget) {
                    probe.step()
                }
                if (!probe.missedTarget) {
                    successCount++
                }
                initialVelocity = Point(initialVelocity.x, initialVelocity.y - 1)
                if (initialVelocity.y < -10000) {
                    initialVelocity = Point(initialVelocity.x - 1, 10000)
                }
            } while (initialVelocity.x > 0)
            successCount
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 45)

    val input = readInput("Day17")
    println(part1(input))

    check(part2(testInput) == 112)
    println(part2(input))
}
