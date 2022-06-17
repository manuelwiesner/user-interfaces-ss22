package at.tugraz.user_interfaces_ss22.rlbot

import rlbot.BaseBot
import rlbot.ControllerState
import rlbot.flat.GameTickPacket

/** This is queried by the framework for the [ControllerState], here we return our custom implementation. */
class GloveBot(
    index: Int,
    team: Int,
    private val gloveState: ControllerState,
) : BaseBot(index, team) {
    override fun processInput(request: GameTickPacket?): ControllerState = this.gloveState
}
