package at.tugraz.user_interfaces_ss22.glove

import rlbot.ControllerState

data class GloveState(
    private val steer: Float = 0f,
    private val throttle: Float = 0f,
    private val pitch: Float = 0f,
    private val yaw: Float = 0f,
    private val roll: Float = 0f,
    private val jump: Boolean = false,
    private val boost: Boolean = false,
    private val handbrake: Boolean = false,
    private val useItem: Boolean = false,
) : ControllerState {

    override fun getSteer(): Float = this.steer

    override fun getThrottle(): Float = this.throttle

    override fun getPitch(): Float = this.pitch

    override fun getYaw(): Float = this.yaw

    override fun getRoll(): Float = this.roll

    override fun holdJump(): Boolean = this.jump

    override fun holdBoost(): Boolean = this.boost

    override fun holdHandbrake(): Boolean = this.handbrake

    override fun holdUseItem(): Boolean = this.useItem
}
