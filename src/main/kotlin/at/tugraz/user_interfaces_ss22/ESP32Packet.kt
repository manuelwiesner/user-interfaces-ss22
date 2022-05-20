package at.tugraz.user_interfaces_ss22

import java.nio.ByteBuffer

/** Represents a 'packet' received from the ESP32, lists the inputs from it. */
data class ESP32Packet(
    val fingerOne: Int,
    val fingerTwo: Int,
    val fingerThree: Int,
    val fingerFour: Int,
    val fingerFive: Int,
    val axisX: Int,
    val axisY: Int,
    val axisZ: Int,
) {
    companion object {
        /** Prefix and postfix to ensure valid position of packet. */
        private const val MAGIC_BYTE: Byte = 66

        /** Number of bytes per input, ESP32 uses 4096 values and fits in two bytes (short). */
        private const val BYTES_PER_INPUT = 2

        /** Number of inputs, equal to the number of fields in the data class. */
        private const val NUM_OF_INPUTS = 8

        /** Takes the i-th input (short) in the buffer and converts it to an Int. */
        private fun ByteBuffer.take(): Int = short.toInt()

        /** Number of bytes a packet has and/or a buffer should have. */
        const val BYTES_PACKET_SIZE = BYTES_PER_INPUT * NUM_OF_INPUTS + 2

        /** Constructs a packet from a byte array, checks the size and magic bytes to ensure valid data. */
        fun constructFromBytes(bytes: ByteArray): ESP32Packet {
            if (bytes.size != BYTES_PACKET_SIZE)
                throw IllegalStateException("ByteArray has invalid size: ${bytes.size}")

            if (bytes[0] != MAGIC_BYTE || bytes[bytes.size - 1] != MAGIC_BYTE)
                throw throw IllegalStateException("Magic byte missing: ${bytes.joinToString()}")

            return with(ByteBuffer.wrap(bytes, 1, bytes.size - 2)) {
                ESP32Packet(take(), take(), take(), take(), take(), take(), take(), take())
            }
        }
    }
}
