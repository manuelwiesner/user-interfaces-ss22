package at.tugraz.user_interfaces_ss22.mapping

import at.tugraz.user_interfaces_ss22.glove.GloveController
import at.tugraz.user_interfaces_ss22.glove.GlovePacket
import rlbot.ControllerState
import rlbot.flat.Rotator

/** Custom [ControllerState], mapping the glove input to rlbot readable values. Also applies calibration LUTs. */
class GloveState(
    private val gloveController: GloveController,
) : ControllerState {

    private val fingerOne = BendCalibrationLookUpTable()
    private val fingerTwo = BendCalibrationLookUpTable()
    private val fingerThree = BendCalibrationLookUpTable()
    private val fingerFour = BendCalibrationLookUpTable()
    private val fingerFive = BendCalibrationLookUpTable()

    // --------------------------------------------------------

    private var latestState: GlovePacket = this.gloveController.state

    private var pitch: Float = 0f
    private var yaw: Float = 0f
    private var roll: Float = 0f

    private var finalPitch: Float = 0f
    private var finalYaw: Float = 0f
    private var finalRoll: Float = 0f

    fun update(rotation: Rotator?): GloveState {
        this.latestState = this.gloveController.state

        this.pitch = rotation?.pitch()?.times(57.29578f)?.plus(180f) ?: 0f
        this.yaw = rotation?.yaw()?.times(57.29578f)?.plus(180f) ?: 0f
        this.roll = rotation?.roll()?.times(57.29578f)?.plus(180f) ?: 0f

        // TODO: pitch / yaw / roll from glove are not in 0..360 degrees (!!)
        // they are in -90..90, -180..180 and 0..infinity depending on which axis
        // plus calculation whether to turn in a direction is not completely
        // correct
        this.finalPitch = (this.pitch - this.latestState.pitch)
        this.finalYaw = (this.yaw - this.latestState.yaw)
        this.finalRoll = (this.roll - this.latestState.roll)
        return this
    }

    override fun getThrottle(): Float {
        val positive = this.fingerOne.mapInput(this.latestState, this.latestState.fingerOne)
        val negative = this.fingerFive.mapInput(this.latestState, this.latestState.fingerFive)
        return (positive - negative) / 4095f
    }

    override fun getSteer(): Float = this.latestState.rollOld
    override fun getPitch(): Float = this.latestState.pitchOld
    override fun getYaw(): Float = this.latestState.yawOld
    override fun getRoll(): Float = this.latestState.rollOld

    override fun holdJump(): Boolean = this.fingerFour.mapInputBoolean(this.latestState.fingerFour)
    override fun holdBoost(): Boolean = this.fingerTwo.mapInputBoolean(this.latestState.fingerTwo)
    override fun holdHandbrake(): Boolean = this.fingerThree.mapInputBoolean(this.latestState.fingerThree)
    override fun holdUseItem(): Boolean = false

    @Throws(IndexOutOfBoundsException::class)
    private fun CalibrationLookUpTable.mapInputBoolean(input: Short): Boolean = mapInput(latestState, input) > 2047

    @Throws(IndexOutOfBoundsException::class)
    private fun CalibrationLookUpTable.mapInputFloat(input: Short): Float = (mapInput(latestState, input) / 4095f) * 2 - 1
}
