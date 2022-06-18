package at.tugraz.user_interfaces_ss22.mapping

import at.tugraz.user_interfaces_ss22.glove.GloveController
import at.tugraz.user_interfaces_ss22.glove.GlovePacket
import rlbot.ControllerState

/** Custom [ControllerState], mapping the glove input to rlbot readable values. Also applies calibration LUTs. */
class GloveState(
    private val gloveController: GloveController,
) : ControllerState {

    private val fingerOne = BendCalibrationLookUpTable()
    private val fingerTwo = BendCalibrationLookUpTable()
    private val fingerThree = BendCalibrationLookUpTable()
    private val fingerFour = BendCalibrationLookUpTable()
    private val fingerFive = BendCalibrationLookUpTable()

    private val gyroX = AxisCalibrationLookUpTable()
    private val gyroY = AxisCalibrationLookUpTable()
    private val gyroZ = AxisCalibrationLookUpTable()

    private val accX = AccCalibrationLookUpTable()
    private val accY = AccCalibrationLookUpTable()
    private val accZ = AccCalibrationLookUpTable()

    // --------------------------------------------------------

    private var latestState: GlovePacket = this.gloveController.state

    override fun getThrottle(): Float {
        this.latestState = this.gloveController.state
        val positive = this.fingerOne.mapInput(this.latestState, this.latestState.fingerOne)
        val negative = this.fingerFive.mapInput(this.latestState, this.latestState.fingerFive)
        return (positive - negative) / 4095f
    }

    override fun getSteer(): Float = this.gyroY.mapInputFloat(this.latestState.gyroY)
    override fun getPitch(): Float = this.gyroX.mapInputFloat(this.latestState.gyroX)
    override fun getYaw(): Float = this.gyroY.mapInputFloat(this.latestState.gyroY)
    override fun getRoll(): Float = this.gyroZ.mapInputFloat(this.latestState.gyroZ)

    override fun holdJump(): Boolean {
        val valueAccX = this.accX.mapInput(this.latestState, this.latestState.accX)
        val valueAccY = this.accY.mapInput(this.latestState, this.latestState.accY)
        val valueAccZ = this.accZ.mapInput(this.latestState, this.latestState.accZ)
        return valueAccX + valueAccY + valueAccZ >= 6144
    }

    override fun holdBoost(): Boolean = this.fingerTwo.mapInputBoolean(this.latestState.fingerTwo)
    override fun holdHandbrake(): Boolean = this.fingerThree.mapInputBoolean(this.latestState.fingerThree)
    override fun holdUseItem(): Boolean = this.fingerFour.mapInputBoolean(this.latestState.fingerFour)

    @Throws(IndexOutOfBoundsException::class)
    private fun CalibrationLookUpTable.mapInputBoolean(input: Short): Boolean = mapInput(latestState, input) > 2047

    @Throws(IndexOutOfBoundsException::class)
    private fun CalibrationLookUpTable.mapInputFloat(input: Short): Float = (mapInput(latestState, input) / 4095f) * 2 - 1

}
