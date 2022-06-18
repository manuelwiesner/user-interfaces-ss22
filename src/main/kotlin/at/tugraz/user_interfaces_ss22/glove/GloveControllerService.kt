package at.tugraz.user_interfaces_ss22.glove

import at.tugraz.user_interfaces_ss22.BaseService
import java.net.ServerSocket

/** Handles the socket connection to the glove, can read [GlovePacket] packets. */
class GloveControllerService(
    private val port: Int,
) : GloveController, BaseService() {

    @Volatile
    override var state: GlovePacket = GlovePacket.EMPTY_PACKET
        private set

    override fun startService() {
        ServerSocket(this.port).use { serverSocket ->
            while (this.running.get()) {
                this.logger.info("Listening for glove on port=$port")
                try {
                    serverSocket
                        .accept()
                        .apply {
                            setSoLinger(true, 0)  // we can destroy TCP socket immediately
                            tcpNoDelay = true               // we need good latency
                        }
                        .use { clientSocket ->
                            this.logger.info("Established connection with glove")

                            val packetBuffer = ByteArray(GlovePacket.BYTES_PACKET_SIZE)
                            val inputStream = clientSocket.getInputStream()

                            while (this.running.get()) {
                                val readBytes = inputStream!!.read(packetBuffer)
                                require(readBytes == packetBuffer.size) { "Read $readBytes bytes, expected ${packetBuffer.size}" }
                                this.state = GlovePacket.constructFromBytes(packetBuffer)
                            }
                        }
                } catch (e: Exception) {
                    this.logger.warn("Connection failed", e)
                }
            }
        }
    }
}
