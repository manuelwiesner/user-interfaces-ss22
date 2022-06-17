package at.tugraz.user_interfaces_ss22.glove

import java.nio.ByteBuffer

/** Represents a 'packet' received from the Glove, lists the inputs from it. */
data class GlovePacket(
    val packetId: Byte,
    val fingerOne: Short,
    val fingerTwo: Short,
    val fingerThree: Short,
    val fingerFour: Short,
    val fingerFive: Short,
    val gyroX: Short,
    val gyroY: Short,
    val gyroZ: Short,
    val accX: Short,
    val accY: Short,
    val accZ: Short,
    val timeConstant: Int,
    val reservedS: Short = 0,
    val reservedB: Byte = 0,
) {
    companion object {
        /** Prefix and postfix to ensure valid position of packet. */
        private const val MAGIC_BYTE: Byte = 42

        /** Number of bytes a packet has and/or a buffer should have (magic byte + variable sizes + magic byte). */
        const val BYTES_PACKET_SIZE = 1 + 1 + 5 * 2 + 3 * 2 + 3 * 2 + 4 + 2 + 1 + 1

        /** Empty state, the car will not move with these values. */
        val EMPTY_PACKET = GlovePacket(0, 0, 0, 0, 0, 0, 2048, 2048, 2048, 0, 0, 0, 0)

        /** Constructs a packet from a byte array, checks the size and magic bytes to ensure valid data. */
        fun constructFromBytes(bytes: ByteArray): GlovePacket {
            if (bytes.size != BYTES_PACKET_SIZE)
                throw IllegalStateException("ByteArray has invalid size: ${bytes.size}")
            if (bytes[0] != MAGIC_BYTE || bytes[bytes.size - 1] != MAGIC_BYTE)
                throw throw IllegalStateException("Magic byte missing: ${bytes.joinToString()}")

            return with(ByteBuffer.wrap(bytes, 1, bytes.size - 2)) {
                GlovePacket(get(), short, short, short, short, short, short, short, short, short, short, short, int, short, get())
            }
        }
    }

    fun jump(): Boolean {
        // TODO better detection
        return this.accX + this.accY + this.accZ >= 6144
    }
}
