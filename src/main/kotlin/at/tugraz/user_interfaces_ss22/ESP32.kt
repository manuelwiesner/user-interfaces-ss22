package at.tugraz.user_interfaces_ss22

import java.io.IOException
import java.io.InputStream
import java.lang.System.Logger
import java.net.ServerSocket

/** Handles the socket connection to the ESP32 and can read [ESP32Packet] packets. */
class ESP32(
    private val port: Int,
) : AutoCloseable {

    private val logger: Logger = System.getLogger("ESP32")
    private val serverSocket: ServerSocket = ServerSocket(this.port)
    private val packetBuffer: ByteArray = ByteArray(ESP32Packet.BYTES_PACKET_SIZE)

    private var inputStream: InputStream? = null

    private fun ensureConnected() {
        if (this.inputStream != null) return

        this.logger.log(Logger.Level.INFO, "Listening for ESP32 on port $port...")

        val socket = this.serverSocket
            .accept()
            .apply {
                setSoLinger(true, 0)  // we can destroy TCP socket immediately
                tcpNoDelay = true               // we need good latency
            }

        this.inputStream = socket.getInputStream()

        this.logger.log(Logger.Level.INFO, "Established connection with ESP32 on $socket")
    }

    /** If not already connected, establish connection to the ESP32 and read a packet. */
    @Throws(IOException::class)
    fun read(): ESP32Packet {
        return kotlin.runCatching {
            ensureConnected()
            this.inputStream!!.read(this.packetBuffer)
            ESP32Packet.constructFromBytes(this.packetBuffer)
        }.getOrElse {
            this.inputStream?.close()
            this.inputStream = null
            this.logger.log(Logger.Level.ERROR, "Failed to connect or read from ESP32!", it)
            throw it
        }
    }

    override fun close() {
        this.inputStream?.close() // closes the client socket as well
        this.serverSocket.close()
    }
}
