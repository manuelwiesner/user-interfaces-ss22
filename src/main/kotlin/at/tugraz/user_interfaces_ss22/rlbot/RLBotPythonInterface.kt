package at.tugraz.user_interfaces_ss22.rlbot

import at.tugraz.user_interfaces_ss22.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rlbot.Bot
import rlbot.ControllerState
import rlbot.manager.BotManager
import rlbot.pyinterop.SocketServer
import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicBoolean

/** Listens for incoming requests from the rlbot python framework and initializes bots if requested. */
class RLBotPythonInterface(
    private val port: Int,
    refreshRate: Int,
    private val gloveStateProvider: (Int) -> ControllerState,
    private val botManager: BotManager = BotManager().apply { setRefreshRate(refreshRate) }
) : SocketServer(port, botManager), Service {

    override val logger: Logger = LoggerFactory.getLogger(this::class.simpleName)
    override val running = AtomicBoolean(false)

    override fun start() {
        if (this.running.getAndSet(true)) throw IllegalStateException("Service is already running")
        this.logger.debug("Starting service")

        this.logger.info("Listening for commands on port=$port")

        // code mostly copied from super class, but could not be reused since it uses a while(true) loop
        ServerSocket(this.port).use { serverSocket ->
            while (this.running.get()) {
                try {
                    serverSocket.accept().use { clientSocket ->
                        val buffer = ByteArray(clientSocket.receiveBufferSize)
                        val bytesRead = clientSocket.getInputStream().read(buffer)
                        val command = String(buffer, 0, bytesRead).split('\n')

                        this.logger.trace("Received command $command")

                        when (command.firstOrNull()) {
                            "add" -> {
                                ensureStarted(command[4])
                                ensureBotRegistered(command[3].toInt(), command[1], command[2].toInt())
                            }
                            "addsocket" -> {
                                ensureSocketStarted(command[4], command[5].toInt())
                                ensureBotRegistered(command[3].toInt(), command[1], command[2].toInt())
                            }
                            "remove" -> {
                                retireBot(command[1].toInt())
                            }
                            else -> throw IllegalArgumentException("Unknown command: $command")
                        }
                    }
                } catch (e: Exception) {
                    this.logger.warn("Receiving command failed", e)
                }
            }
        }
    }

    override fun initBot(index: Int, botType: String?, team: Int): Bot {
        this.logger.info("Creating a new bot instance name=$botType, playerIndex=$index, team=$team")
        return GloveBot(index, team, this.gloveStateProvider(index))
    }

    override fun shutdown() {
        this.logger.debug("Retired all bots, waiting for new commands")
    }

    override fun close() {
        this.botManager.shutDown()
        this.logger.debug("Stopped service")
        this.running.set(false)
    }
}
