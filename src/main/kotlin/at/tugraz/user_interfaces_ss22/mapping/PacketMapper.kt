package at.tugraz.user_interfaces_ss22.mapping

import at.tugraz.user_interfaces_ss22.glove.GlovePacket
import at.tugraz.user_interfaces_ss22.glove.GloveState
import rlbot.flat.Rotator
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sign
import kotlin.math.sqrt

class PacketMapper {
    companion object {
        /** How many packets will be used for calibration */
        const val CALIBRATION_PACKET_NUM: Int = 500

        /** Convert radians to degree */
        const val RAD_2_DEG: Float = 57.29578f

        /** Accelerometer constant set in ESP code */
        const val ACC_CONSTANT: Float = 4096.0f

        /** Gyroscope constant set in ESP code */
        const val GYRO_CONSTANT: Float = 65.5f

        /** Convert time to correct unit */
        const val TIME_CONVERSION: Float = 0.001f

        /** Convert -180..180 to 0..360 */
        const val ANGULAR_OFFSET: Float = 180f

        /** COMPLEMENTARY_FILTER_COEFFICIENT */
        const val C_F_C: Float = 0.98f
    }

    // --------------------------------------------------------

    private val fingerOneLUT = BendCalibrationLookUpTable()
    private val fingerTwoLUT = BendCalibrationLookUpTable()
    private val fingerThreeLUT = BendCalibrationLookUpTable()
    private val fingerFourLUT = BendCalibrationLookUpTable()
    private val fingerFiveLUT = BendCalibrationLookUpTable()

    // --------------------------------------------------------

    private var carPitch: Float = 0f
    private var carYaw: Float = 0f
    private var carRoll: Float = 0f

    // --------------------------------------------------------

    private var offsetGyroX: Float = 0f
    private var offsetGyroY: Float = 0f
    private var offsetGyroZ: Float = 0f

    private var offsetAccX: Float = 0f
    private var offsetAccY: Float = 0f
    private var offsetAccZ: Float = 0f

//    private var gyroX: Float = 0f
//    private var gyroY: Float = 0f
//    private var gyroZ: Float = 0f
//
//    private var accX: Float = 0f
//    private var accY: Float = 0f
//    private var accZ: Float = 0f

    private var angleX: Float = 0f
    private var angleY: Float = 0f
    private var angleZ: Float = 0f

    private var lastTimeConstant: Int = 0

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

    val finalPitch: Float
        get() = (this.pitch - this.carPitch) / 20f
    val finalYaw: Float
        get() = (this.yaw - this.carYaw) / 20f
    val finalRoll: Float
        get() = (this.roll - this.carRoll) / 20f

    @Volatile
    private var latestState: GlovePacket = GlovePacket()

    // --------------------------------------------------------

    @Volatile
    var state: GloveState = GloveState()
        private set

    fun startNewGloveConnection() {
        this.offsetGyroX = 0f
        this.offsetGyroY = 0f
        this.offsetGyroZ = 0f

        this.offsetAccX = 0f
        this.offsetAccY = 0f
        this.offsetAccZ = 0f

        this.angleX = 0f
        this.angleY = 0f
        this.angleZ = 0f

        this.pitch = 0f
        this.yaw = 0f
        this.roll = 0f

        this.pitchOld = 0f
        this.yawOld = 0f
        this.rollOld = 0f

        this.lastTimeConstant = 0

        this.latestState = GlovePacket()
    }

    fun updateCarState(rotation: Rotator?) {
        if (rotation == null) {
            this.carPitch = 0f
            this.carYaw = 0f
            this.carRoll = 0f
        } else {
            this.carPitch = rotation.pitch() * RAD_2_DEG + ANGULAR_OFFSET
            this.carYaw = rotation.yaw() * RAD_2_DEG + ANGULAR_OFFSET
            this.carRoll = rotation.roll() * RAD_2_DEG + ANGULAR_OFFSET
            this.state = GloveState(
                steer = this.rollOld,
                throttle = getThrottle(),
                pitch = this.pitchOld,
                yaw = this.yawOld,
                roll = this.rollOld,
                jump = this.fingerFourLUT.mapInputBoolean(this.latestState.fingerFour),
                boost = this.fingerTwoLUT.mapInputBoolean(this.latestState.fingerTwo),
                handbrake = this.fingerThreeLUT.mapInputBoolean(this.latestState.fingerThree),
                useItem = false,
            )
        }
    }

    fun calibrateGlove(packets: List<GlovePacket>) {
        if (packets.size != CALIBRATION_PACKET_NUM) throw IllegalArgumentException("Calibration invalid packets size: ${packets.size}")

        var tmpOffsetGyroX = 0f
        var tmpOffsetGyroY = 0f
        var tmpOffsetGyroZ = 0f
        var tmpOffsetAccX = 0f
        var tmpOffsetAccY = 0f
        var tmpOffsetAccZ = 0f

        packets.forEach { packet ->
            updateGloveState(packet)
            tmpOffsetGyroX += packet.getGyroX(0f)
            tmpOffsetGyroY += packet.getGyroY(0f)
            tmpOffsetGyroZ += packet.getGyroZ(0f)
            tmpOffsetAccX += packet.getAccX(0f)
            tmpOffsetAccY += packet.getAccY(0f)
            tmpOffsetAccZ += packet.getAccZ(0f)
        }

        this.offsetGyroX = tmpOffsetGyroX / CALIBRATION_PACKET_NUM
        this.offsetGyroY = tmpOffsetGyroY / CALIBRATION_PACKET_NUM
        this.offsetGyroZ = tmpOffsetGyroZ / CALIBRATION_PACKET_NUM
        this.offsetAccX = tmpOffsetAccX / CALIBRATION_PACKET_NUM
        this.offsetAccY = tmpOffsetAccY / CALIBRATION_PACKET_NUM
        this.offsetAccZ = tmpOffsetAccZ / CALIBRATION_PACKET_NUM
    }

    fun updateGloveState(packet: GlovePacket) {
        val gyroX = packet.getGyroX(0f)
        val gyroY = packet.getGyroY(0f)
        val gyroZ = packet.getGyroZ(0f)
        val accX = packet.getAccX(0f)
        val accY = packet.getAccY(0f)
        val accZ = packet.getAccZ(0f)

        val angleAccX = atan2(accY, accZ.sign * sqrt(accZ * accZ + accX * accX)) * RAD_2_DEG
        val angleAccY = -atan2(accX, sqrt(accZ * accZ + accY * accY)) * RAD_2_DEG

        val dt = (packet.timeConstant - lastTimeConstant) * TIME_CONVERSION

        this.angleX = C_F_C * (this.angleX + gyroX * dt) + (1f - C_F_C) * angleAccX
        this.angleY = C_F_C * (this.angleY + gyroY * dt) + (1f - C_F_C) * angleAccY
        this.angleZ += gyroZ * dt

        this.pitch = this.angleY / 2f + 180f
        this.yaw = (this.angleZ % 360f)
        this.roll = -this.angleX + 180f

        this.pitchOld = this.angleY / 90f
        this.yawOld = ((abs(this.angleZ) % 360f) - 180f) / 180f
        this.rollOld = this.angleX / 180f

        this.lastTimeConstant = packet.timeConstant
    }


    private fun getThrottle(): Float {
        val positive = this.fingerOneLUT.mapInput(this.latestState, this.latestState.fingerOne)
        val negative = this.fingerFiveLUT.mapInput(this.latestState, this.latestState.fingerFive)
        return (positive - negative) / 4095f
    }

    @Throws(IndexOutOfBoundsException::class)
    private fun CalibrationLookUpTable.mapInputBoolean(input: Short): Boolean = mapInput(latestState, input) > 2047

    @Throws(IndexOutOfBoundsException::class)
    private fun CalibrationLookUpTable.mapInputFloat(input: Short): Float = (mapInput(latestState, input) / 4095f) * 2 - 1
}
