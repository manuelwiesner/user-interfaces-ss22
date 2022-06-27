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

        private const val ACC_CONSTANT: Float = 4096.0f
        private const val GYRO_CONSTANT: Float = 65.5f
        private const val RAD_2_DEG: Float = 57.29578f
        private const val TIME_CONVERSION: Float = 0.001f

        /** COMPLEMENTARY_FILTER_COEFFICIENT */
        private const val CFC: Float = 0.98f
    }

    var accX: Float = 0f
        private set
    var accY: Float = 0f
        private set
    var accZ: Float = 0f
        private set

    var gyroX: Float = 0f
        private set
    var gyroY: Float = 0f
        private set
    var gyroZ: Float = 0f
        private set

    var angleX: Float = 0f
        private set
    var angleY: Float = 0f
        private set
    var angleZ: Float = 0f
        private set

    var pitch: Float = 0f
        private set
    var yaw: Float = 0f
        private set
    var roll: Float = 0f
        private set

    var pitchOld: Float = 0f
        private set
    var yawOld: Float = 0f
        private set
    var rollOld: Float = 0f
        private set

    fun process(
        offsetGyroX: Float,
        offsetGyroY: Float,
        offsetGyroZ: Float,
        offsetAccX: Float,
        offsetAccY: Float,
        offsetAccZ: Float,
        lastAngleX: Float,
        lastAngleY: Float,
        lastAngleZ: Float,
        lastTimeConstant: Int
    ) {
        this.gyroX = this.rawGyroX / GYRO_CONSTANT - offsetGyroX
        this.gyroY = this.rawGyroY / GYRO_CONSTANT - offsetGyroY
        this.gyroZ = this.rawGyroZ / GYRO_CONSTANT - offsetGyroZ
        this.accX = this.rawAccX / ACC_CONSTANT - offsetAccX
        this.accY = this.rawAccY / ACC_CONSTANT - offsetAccY
        this.accZ = this.rawAccZ / ACC_CONSTANT - offsetAccZ

        val angleAccX = atan2(accY, accZ.sign * sqrt(accZ * accZ + accX * accX)) * RAD_2_DEG
        val angleAccY = -atan2(accX, sqrt(accZ * accZ + accY * accY)) * RAD_2_DEG

        val dt = (this.timeConstant - lastTimeConstant) * TIME_CONVERSION

        this.angleX = CFC * (lastAngleX + gyroX * dt) + (1f - CFC) * angleAccX
        this.angleY = CFC * (lastAngleY + gyroY * dt) + (1f - CFC) * angleAccY
        this.angleZ = lastAngleZ + gyroZ * dt

        this.pitch = this.angleY * 2f + 180f
        this.yaw = (this.angleZ % 360f)
        this.roll = this.angleX + 180f

        this.pitchOld = this.angleY / 90f
        this.yawOld = ((abs(this.angleZ) % 360f) - 180f) / 180f
        this.rollOld = -this.angleX / 180f
    }

    override fun toString(): String {
        return "GlovePacket(" +
                "packetId=$packetId, " +
                "fingerOne=$fingerOne, " +
                "fingerTwo=$fingerTwo, " +
                "fingerThree=$fingerThree, " +
                "fingerFour=$fingerFour, " +
                "fingerFive=$fingerFive, " +
                "pitch=$pitchOld, " +
                "yaw=$yawOld, " +
                "roll=$rollOld" +
                ")"
    }

    fun getGyroX(offset: Float): Float = this.rawGyroX / PacketMapper.GYRO_CONSTANT - offset

    fun getGyroY(offset: Float): Float = this.rawGyroY / PacketMapper.GYRO_CONSTANT - offset

    fun getGyroZ(offset: Float): Float = this.rawGyroZ / PacketMapper.GYRO_CONSTANT - offset

    fun getAccX(offset: Float): Float = this.rawAccX / PacketMapper.ACC_CONSTANT - offset

    fun getAccY(offset: Float): Float = this.rawAccY / PacketMapper.ACC_CONSTANT - offset

    fun getAccZ(offset: Float): Float = this.rawAccZ / PacketMapper.ACC_CONSTANT - offset
}
