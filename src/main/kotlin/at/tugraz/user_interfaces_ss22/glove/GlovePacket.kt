package at.tugraz.user_interfaces_ss22.glove

import at.tugraz.user_interfaces_ss22.mapping.PacketMapper
import java.nio.ByteBuffer

/** Represents a 'packet' received from the Glove, lists the inputs from it. */
data class GlovePacket(
    val packetId: Byte = 0,
    val fingerOne: Short = 0,
    val fingerTwo: Short = 0,
    val fingerThree: Short = 0,
    val fingerFour: Short = 0,
    val fingerFive: Short = 0,
    val rawGyroX: Short = 0,
    val rawGyroY: Short = 0,
    val rawGyroZ: Short = 0,
    val rawAccX: Short = 0,
    val rawAccY: Short = 0,
    val rawAccZ: Short = 0,
    val timeConstant: Int = 0,
    val reservedS: Short = 0,
    val reservedB: Byte = 0,
) {
    companion object {
        /** Prefix and postfix to ensure valid position of packet. */
        private const val MAGIC_BYTE: Byte = 42

        /** Number of bytes a packet has and/or a buffer should have (magic byte + variable sizes + magic byte). */
        const val BYTES_PACKET_SIZE = 1 + 1 + 5 * 2 + 3 * 2 + 3 * 2 + 4 + 2 + 1 + 1

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

    fun getGyroX(offset: Float): Float = this.rawGyroX / PacketMapper.GYRO_CONSTANT - offset

    fun getGyroY(offset: Float): Float = this.rawGyroY / PacketMapper.GYRO_CONSTANT - offset

    fun getGyroZ(offset: Float): Float = this.rawGyroZ / PacketMapper.GYRO_CONSTANT - offset

    fun getAccX(offset: Float): Float = this.rawAccX / PacketMapper.ACC_CONSTANT - offset

    fun getAccY(offset: Float): Float = this.rawAccY / PacketMapper.ACC_CONSTANT - offset

    fun getAccZ(offset: Float): Float = this.rawAccZ / PacketMapper.ACC_CONSTANT - offset
}
