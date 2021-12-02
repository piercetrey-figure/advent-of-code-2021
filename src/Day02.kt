private enum class DirectionType {
    Horizontal,
    Vertical
}

private enum class Direction(val key: String, val type: DirectionType, val polarity: Int) {
    Forward("forward", DirectionType.Horizontal, 1),
    Down("down", DirectionType.Vertical, 1),
    Up("up", DirectionType.Vertical, -1),
}

private open class Position(val horizontal: Int, val depth: Int) {
    val result: Int
        get() = horizontal * depth

    open fun move(command: String): Position = command.parseMovement().let {
        if (it.direction.type == DirectionType.Horizontal) {
            Position(horizontal + (it.amount * it.direction.polarity), depth)
        } else {
            Position(horizontal, depth + (it.amount * it.direction.polarity))
        }
    }
}

private class AimPosition(horizontal: Int, depth: Int, val aim: Int): Position(horizontal, depth) {
    override fun move(command: String): AimPosition = command.parseMovement().let {
        if (it.direction.type == DirectionType.Horizontal) {
            AimPosition(horizontal + (it.amount * it.direction.polarity), depth + (it.amount * aim), aim)
        } else {
            AimPosition(horizontal, depth, aim + (it.amount * it.direction.polarity))
        }
    }
}

private data class Movement(val direction: Direction, val amount: Int)

private fun String.parseMovement(): Movement = split(' ').let {
    assert(it.size == 2) { "invalid movement $this" }
    Movement(it.first().let { dir -> Direction.values().find { it.key == dir }}!!, it.last().toInt())
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.fold(Position(0, 0)) { acc, movement ->
            acc.move(movement)
        }.result
    }

    fun part2(input: List<String>): Int {
        return input.fold(AimPosition(0, 0, 0)) { acc, movement ->
            acc.move(movement)
        }.result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)

    val input = readInput("Day02")
    println(part1(input))

    check(part2(testInput) == 900)
    println(part2(input))
}
