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

    private val axisAcc = AxisAccCalibrationLookUpTable()

    // --------------------------------------------------------

    private var latestState: GlovePacket = this.gloveController.state

    //TODO: use AxisAcc updateData function after every new glove packet


    override fun getThrottle(): Float {
        this.latestState = this.gloveController.state
        val positive = this.fingerOne.mapInput(this.latestState, this.latestState.fingerOne)
        val negative = this.fingerFive.mapInput(this.latestState, this.latestState.fingerFive)
        return (positive - negative) / 4095f
    }

    override fun getSteer(): Float = axisAcc.angleY
    override fun getPitch(): Float = axisAcc.angleX
    override fun getYaw(): Float = axisAcc.angleY
    override fun getRoll(): Float = axisAcc.angleZ

    override fun holdJump(): Boolean {
        val valueAccX = axisAcc.angleAccX
        val valueAccY = axisAcc.angleAccY
        return valueAccX + valueAccY >= 6144 //TODO: still right value?
    }

    override fun holdBoost(): Boolean = this.fingerTwo.mapInputBoolean(this.latestState.fingerTwo)
    override fun holdHandbrake(): Boolean = this.fingerThree.mapInputBoolean(this.latestState.fingerThree)
    override fun holdUseItem(): Boolean = this.fingerFour.mapInputBoolean(this.latestState.fingerFour)

    @Throws(IndexOutOfBoundsException::class)
    private fun CalibrationLookUpTable.mapInputBoolean(input: Short): Boolean = mapInput(latestState, input) > 2047

    @Throws(IndexOutOfBoundsException::class)
    private fun CalibrationLookUpTable.mapInputFloat(input: Short): Float = (mapInput(latestState, input) / 4095f) * 2 - 1

}
