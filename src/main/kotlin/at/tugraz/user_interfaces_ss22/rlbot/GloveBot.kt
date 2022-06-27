package at.tugraz.user_interfaces_ss22.rlbot

import at.tugraz.user_interfaces_ss22.glove.GloveController
import rlbot.BaseBot
import rlbot.ControllerState
import rlbot.flat.GameTickPacket
import rlbot.flat.Physics
import rlbot.flat.PlayerInfo
import rlbot.flat.Rotator

/** This is queried by the framework for the [ControllerState], here we return our custom implementation. */
class GloveBot(
    index: Int,
    team: Int,
    private val gloveController: GloveController,
) : BaseBot(index, team) {

    private val playerInfoTable: PlayerInfo = PlayerInfo()
    private val physicsTable: Physics = Physics()
    private val rotationTable: Rotator = Rotator()

    override fun processInput(request: GameTickPacket?): ControllerState {
        val playerInfo = request?.takeIf { it.playersLength() > this.index }?.players(this.playerInfoTable, this.index)
        val physics = playerInfo?.physics(this.physicsTable)
        val rotation = physics?.rotation(this.rotationTable)
        return this.gloveController.updateCar(rotation).state
    }
}
