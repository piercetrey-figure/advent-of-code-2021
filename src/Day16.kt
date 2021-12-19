fun packetFactory(bits: String): Packet {
    val typeId = bits.drop(3).take(3).toInt(2)
    return if (typeId == Packet.LITERAL) {
        LiteralPacket(bits)
    } else {
        OperatorPacket(bits)
    }
}

sealed class Packet(val bits: String) {
    companion object {
        val LITERAL = 4
    }
    val version = bits.take(3).toInt(2)
    val typeId = bits.drop(3).take(3).toInt(2)
    open val versionSum: Int = version
    abstract val value: Long
}
class LiteralPacket(bits: String): Packet(bits) {
    override val value = bits.drop(6).let {
        var idx = 0
        var literalBits = ""
        do {
            val curr = it.drop(idx).take(5)
            literalBits += curr.drop(1)
            idx += 5
        } while (curr[0] != '0')
        literalBits.toLong(2)
    }

    override fun toString(): String = "[\n\ttype = LiteralPacket, \n\tbits = $bits, \n\tversion = $version, \n\ttypeId = $typeId, \n\tvalue = $value\n]"
}
class OperatorPacket(bits: String): Packet(bits) {
    val lengthTypeId = bits.drop(6).take(1).toInt(2)
    val lengthSize = when (lengthTypeId) {
        0 -> 15
        1 -> 11
        else -> throw IllegalStateException("Invalid lengthTypeId of $lengthTypeId")
    }
    val length = bits.drop(7).take(lengthSize).toInt(2)

    fun String.takeLiteralPacket(): Pair<String, Packet> {
        var idx = 6
        var packetBits = take(6)
        do {
            val curr = drop(idx).take(5)
            packetBits += curr
            idx += 5
            if (curr.first() == '0') { // last group
                return drop(idx) to packetFactory(packetBits)
            }
        } while (true)
    }

    fun String.takeNPackets(n: Int): Pair<String, List<Packet>> {
        var remaining = this
        val subPackets = mutableListOf<Packet>()
        for (i in 0..n-1) {
            val subPacketLength = remaining.firstSubPacketLength()
            val subBits = remaining.take(subPacketLength)
            remaining = remaining.drop(subPacketLength)
            subPackets.add(packetFactory(subBits))
        }
        return remaining to subPackets.toList()
    }

    fun String.firstSubPacketLength(): Int {
        val subType = drop(3).take(3).toInt(2)
        return when (subType) {
            LITERAL -> takeLiteralPacket().second.bits.length
            else -> {
                val subLengthTypeId = drop(6).take(1).toInt(2)
                when (subLengthTypeId) {
                    0 -> drop(7).take(15).toInt(2) + 7 + 15
                    1 -> drop(7).take(11).toInt(2).let { n ->
                        length - drop(18).takeNPackets(n).first.length
                    }
                    else -> throw IllegalStateException("Invalid subLengthTypeId of $subLengthTypeId")
                }
            }
        }
    }

    fun String.takeAllSubpackets(): List<Packet> {
        var remaining = this
        val subPackets = mutableListOf<Packet>()
        while (remaining.isNotEmpty() && remaining.any { it != '0'}) {
            val subPacketLength = remaining.firstSubPacketLength()
            val subBits = remaining.take(subPacketLength)
            remaining = remaining.drop(subPacketLength)
            subPackets.add(packetFactory(subBits))
        }
        return subPackets.toList()
    }

    val subPackets = bits.drop(7 + lengthSize).takeAllSubpackets()

    override val versionSum: Int = version + subPackets.sumOf {
        it.versionSum
    }

    override val value: Long = when(typeId) {
        0 -> subPackets.sumOf { it.value }
        1 -> subPackets.fold(1) { acc, curr -> acc * curr.value }
        2 -> subPackets.minOf { it.value }
        3 -> subPackets.maxOf { it.value }
        5 -> if (subPackets.first().value > subPackets.last().value) 1 else 0
        6 -> if (subPackets.first().value < subPackets.last().value) 1 else 0
        7 -> if (subPackets.first().value == subPackets.last().value) 1 else 0
        else -> throw IllegalStateException("Invalid typeId of $typeId for Operator")
    }
}

fun String.toPacket(): Packet = split("").filterNot { it.isBlank() }.map {
    it.toInt(16).toString(2).padStart(4, '0')
}.joinToString("")
    .let { packetFactory(it) }

fun main() {
    fun part1(input: String): Int {
        return input.toPacket().versionSum
    }

    fun part2(input: String): Long {
        return input.toPacket().value
    }

    // test if implementation meets criteria from the description, like:
    check(part1("8A004A801A8002F478") == 16)
    check(part1("620080001611562C8802118E34") == 12)
    check(part1("C0015000016115A2E0802F182340") == 23)
    check(part1("A0016C880162017C3686B18A3D4780") == 31)

    val input = readInput("Day16")
    println(part1(input.first()))

    check(part2("C200B40A82") == 3L)
    check(part2("04005AC33890") == 54L)
    check(part2("880086C3E88112") == 7L)
    check(part2("CE00C43D881120") == 9L)
    check(part2("D8005AC2A8F0") == 1L)
    check(part2("F600BC2D8F") == 0L)
    check(part2("9C005AC2F8F0") == 0L)
    check(part2("9C0141080250320F1802104A08") == 1L)
    println(part2(input.first()))
}
