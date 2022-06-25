package at.tugraz.user_interfaces_ss22.glove

import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sign
import kotlin.math.sqrt

/** Represents a 'packet' received from the Glove, lists the inputs from it. */
data class GlovePacket(
    val packetId: Byte,
    val fingerOne: Short,
    val fingerTwo: Short,
    val fingerThree: Short,
    val fingerFour: Short,
    val fingerFive: Short,
    val rawGyroX: Short,
    val rawGyroY: Short,
    val rawGyroZ: Short,
    val rawAccX: Short,
    val rawAccY: Short,
    val rawAccZ: Short,
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
}
