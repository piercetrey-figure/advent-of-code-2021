sealed class SnailNum {
    abstract val magnitude: Int
    abstract val isSimple: Boolean
    abstract fun addToMostRightChild(num: Int)
    abstract fun addToMostLeftChild(num: Int)
    abstract fun doExplode(depth: Int = 0): Pair<Int, Int>?
    abstract fun doSplit(): SnailNum?
    abstract fun reduce(): SnailNum
    var explodeMe = false
        protected set
}
class RegularNum(var value: Int): SnailNum() {
    override fun toString() = value.toString()

    override val magnitude: Int
        get() = value

    override val isSimple = false

    override fun addToMostLeftChild(num: Int) {
        value += num
    }

    override fun addToMostRightChild(num: Int) {
        value += num
    }

    override fun doExplode(depth: Int) = null

    override fun doSplit(): SnailNum? = takeIf { value >= 10 }?.let {
        SnailPair(RegularNum(Math.floor(value.toDouble() / 2).toInt()) to RegularNum(Math.ceil(value.toDouble() / 2).toInt()))
    }

    override fun reduce(): SnailNum = this
}
class SnailPair(var pair: Pair<SnailNum, SnailNum>): SnailNum() {
    override val isSimple: Boolean
        get() = pair.first is RegularNum && pair.second is RegularNum
    override fun toString(): String = "[${pair.first},${pair.second}]"
    
    fun add(other: SnailPair) = SnailPair(this to other)

    override val magnitude: Int
        get() = 3 * pair.first.magnitude + 2 * pair.second.magnitude

    override fun doExplode(depth: Int): Pair<Int, Int>? {
        if (depth >= 4) {
            if (isSimple) {
                explodeMe = true
                return pair.first.magnitude to pair.second.magnitude
            }
        }
        val explodeLeft = pair.first.doExplode(depth + 1)
        if (explodeLeft != null) {
            if (pair.first.explodeMe) {
                pair = RegularNum(0) to pair.second
            }
            pair.second.addToMostLeftChild(explodeLeft.second)
            return explodeLeft.first to 0
        }
        val explodeRight = pair.second.doExplode(depth + 1)
        if (explodeRight != null) {
            if (pair.second.explodeMe) { // immediate child asploded
                pair = pair.first to RegularNum(0)
            }
            pair.first.addToMostRightChild(explodeRight.first) // find nearest neighbor on left
            return 0 to explodeRight.second
        }
        return null
    }

    override fun addToMostRightChild(num: Int) {
        pair.second.addToMostRightChild(num)
    }

    override fun addToMostLeftChild(num: Int) {
        pair.first.addToMostLeftChild(num)
    }

    override fun doSplit(): SnailNum? {
        return pair.first.doSplit()?.let {
            pair = it to pair.second
            this
        } ?: pair.second.doSplit()?.let {
            pair = pair.first to it
            this
        }
    }

    override fun reduce() = apply {
        do {
            val hadAction = if (doExplode() != null) {
                true
            } else if (doSplit() != null) {
                true
            } else {
                false
            }
        } while (hadAction)
    }
}

fun String.toSnailOrInt(): SnailNum {
    if (Regex("[0-9]+").matches(this)) {
        return RegularNum(toInt())
    }
    return toSnailPair()
}

fun String.toSnailPair(): SnailPair {
    var startingBrackets = takeWhile { it == '[' }.length

    if (startingBrackets == 1) { // first is int
        val commaIdx = indexOf(',')
        return SnailPair(this.substring(1, commaIdx).toSnailOrInt() to this.substring(commaIdx + 1).dropLast(1).toSnailOrInt())
    }

    var i = startingBrackets
    while (startingBrackets > 1) {
        when(this.get(i)) {
            ']' -> startingBrackets--
            '[' -> startingBrackets++
        }
        i++
    }
    val left = drop(1).take(i - 1)
    val right = drop(i + 1).dropLast(1)
    return SnailPair(left.toSnailOrInt() to right.toSnailOrInt())
}

fun String.shouldExplode(expected: String) {
    val pair = toSnailPair()
    pair.doExplode()
    val result = pair.toString()
    check(result == expected) { "$this expected to explode to $expected but result was $result" }
}

fun String.shouldSplit(expected: String) {
    val pair = toSnailPair()
    pair.doSplit()
    val result = pair.toString()
    check(result == expected) { "$this expected to split to $expected but result was $result" }
}

fun String.shouldReduce(expected: String) {
    val pair = toSnailPair()
    pair.reduce()
    val result = pair.toString()
    check(result == expected) { "$this expected to reduce to $expected but result was $result" }
}

fun Pair<String, String>.shouldAdd(expected: String) {
    val result = first.toSnailPair().add(second.toSnailPair()).toString()
    check(result == expected) { "$this expected to add to $expected but result was $result" }
}

fun List<String>.shouldSum(expected: String) {
    val result = map { it.toSnailPair().reduce() }.reduce { acc, curr -> acc.add(curr).reduce() }.toString()
    check(result == expected) { "$this expected to sum to $expected but result was $result" }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.toSnailPair().reduce() }.reduce() { acc, curr -> acc.add(curr).reduce() }.magnitude
    }

    fun part2(input: List<String>): Int {
        var max = Int.MIN_VALUE
        for (i in 0..input.size - 1) {
            for (j in 0..input.size - 1) {
                if (i == j) continue
                val s1 = input[i].toSnailPair().reduce()
                val s2 = input[j].toSnailPair().reduce()
                val sum = s1.add(s2).reduce()
                max = Math.max(max, sum.magnitude)
            }
        }
        return max
    }

    "[[[[[9,8],1],2],3],4]".shouldExplode("[[[[0,9],2],3],4]")
    "[7,[6,[5,[4,[3,2]]]]]".shouldExplode("[7,[6,[5,[7,0]]]]")
    "[[6,[5,[4,[3,2]]]],1]".shouldExplode("[[6,[5,[7,0]]],3]")
    "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]".shouldExplode("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
    "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]".shouldExplode("[[3,[2,[8,0]]],[9,[5,[7,0]]]]")
    println("explode werkz")

    "[[[[0,7],4],[15,[0,13]]],[1,1]]".shouldSplit("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]")
    println("split worrkzz")

    "[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]".shouldReduce("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
    println("reduce workz")

    ("[[[[4,3],4],4],[7,[[8,4],9]]]" to "[1,1]").shouldAdd("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]")
    println("add wooorzza")

    listOf("[1,1]","[2,2]","[3,3]","[4,4]").shouldSum("[[[[1,1],[2,2]],[3,3]],[4,4]]")
    listOf("[1,1]","[2,2]","[3,3]","[4,4]","[5,5]").shouldSum("[[[[3,0],[5,3]],[4,4]],[5,5]]")
    listOf("[1,1]","[2,2]","[3,3]","[4,4]","[5,5]","[6,6]").shouldSum("[[[[5,0],[7,4]],[5,5]],[6,6]]")
    println("sum workz")

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140)

    val input = readInput("Day18")
    println(part1(input))

    check(part2(testInput) == 3993)
    println(part2(input))
}
