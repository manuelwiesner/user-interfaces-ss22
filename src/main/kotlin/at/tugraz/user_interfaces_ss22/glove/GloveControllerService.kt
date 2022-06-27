package at.tugraz.user_interfaces_ss22.glove

import at.tugraz.user_interfaces_ss22.BaseService
import at.tugraz.user_interfaces_ss22.mapping.PacketMapper
import rlbot.flat.Rotator
import java.net.ServerSocket

/** Handles the socket connection to the glove, can read [GlovePacket] packets. */
class GloveControllerService(
    private val port: Int,
) : GloveController, BaseService() {

    private val packetMapper: PacketMapper = PacketMapper()

    override val state: GloveState
        get() = this.packetMapper.state

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

                            val calibrationPackets: MutableList<GlovePacket> = ArrayList(PacketMapper.CALIBRATION_PACKET_NUM)
                            val packetBuffer = ByteArray(GlovePacket.BYTES_PACKET_SIZE)
                            val inputStream = clientSocket.getInputStream()

                            var packetId: Byte = 0
                            var i = 0

                            this.packetMapper.startNewGloveConnection()

                            while (this.running.get() && !clientSocket.isClosed) {
                                val readBytes = inputStream!!.read(packetBuffer)
                                require(readBytes == packetBuffer.size) { "Read $readBytes bytes, expected ${packetBuffer.size}" }
                                val packet = GlovePacket.constructFromBytes(packetBuffer)
                                require(packet.packetId == packetId) { "PacketId should be $packetId but is ${packet.packetId}" }

                                when {
                                    i < PacketMapper.CALIBRATION_PACKET_NUM -> calibrationPackets.add(packet)
                                    i == PacketMapper.CALIBRATION_PACKET_NUM -> this.packetMapper.calibrateGlove(calibrationPackets)
                                    else -> this.packetMapper.updateGloveState(packet)
                                }
                                packetId++
                                i++
                            }
                        }
                } catch (e: Exception) {
                    this.logger.warn("Connection failed", e)
                }
            }
        }
    }

    override fun createCar(): GloveController {
        // unused for now, but might be useful later
        return this
    }

    override fun updateCar(rotation: Rotator?): GloveController {
        this.packetMapper.updateCarState(rotation)
        return this
    }
}
