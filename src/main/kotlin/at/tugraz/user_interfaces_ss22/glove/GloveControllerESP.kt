package at.tugraz.user_interfaces_ss22.glove

import java.io.InputStream
import java.net.ServerSocket

/** Handles the socket connection to the glove, can read [GlovePacket] packets. */
class GloveControllerESP(
    private val port: Int,
) : GloveController {

    private val logger: System.Logger = System.getLogger("GloveController")

    private val serverSocket: ServerSocket = ServerSocket(this.port)
    private val packetBuffer: ByteArray = ByteArray(GlovePacket.BYTES_PACKET_SIZE)

    private var inputStream: InputStream? = null

    private var running: Boolean = true

    @Volatile
    override var state: GlovePacket = GlovePacket.EMPTY_PACKET
        private set

    init {
        Thread {
            try {
                while (this.running) {
                    this.state = readPacket()
                }
            } catch (e: Exception) {
                this.running = false
                this.logger.log(System.Logger.Level.ERROR, "Getting the latest packet failed!", e)
            }
        }.start()
    }

    private fun ensureConnected() {
        if (this.inputStream != null) return

        this.logger.log(System.Logger.Level.INFO, "Listening for ESP32 on port $port...")

        val socket = this.serverSocket
            .accept()
            .apply {
                setSoLinger(true, 0)  // we can destroy TCP socket immediately
                tcpNoDelay = true               // we need good latency
            }

        this.inputStream = socket.getInputStream()

        this.logger.log(System.Logger.Level.INFO, "Established connection with ESP32 on $socket")
    }

    private fun readPacket(): GlovePacket {
        return kotlin.runCatching {
            ensureConnected()
            this.inputStream!!.read(this.packetBuffer)
            GlovePacket.constructFromBytes(this.packetBuffer)
        }.getOrElse {
            this.inputStream?.close()
            this.inputStream = null
            this.logger.log(System.Logger.Level.ERROR, "Failed to connect or read from ESP32!", it)
            throw it
        }
    }

    override fun close() {
        this.logger.log(System.Logger.Level.INFO, "Closing GloveController!")
        this.inputStream?.close() // closes the client socket as well
        this.serverSocket.close()
    }
}
