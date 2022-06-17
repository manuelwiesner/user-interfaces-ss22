package at.tugraz.user_interfaces_ss22.rlbot

import rlbot.Bot
import rlbot.ControllerState
import rlbot.manager.BotManager
import rlbot.pyinterop.SocketServer

/** Listens for incoming requests from the rlbot python framework and initializes bots if requested. */
class RLBotPythonInterface(
    portPython: Int,
    refreshRate: Int,
    private val gloveStateProvider: (Int) -> ControllerState,
) : SocketServer(portPython, BotManager().apply { setRefreshRate(refreshRate) }) {

    override fun initBot(index: Int, botType: String?, team: Int): Bot {
        return GloveBot(index, team, this.gloveStateProvider(index))
    }
}
