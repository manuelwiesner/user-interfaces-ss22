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

                            val startTime = System.currentTimeMillis()
                            var i = 0
                            var packetId: Byte = 0

                            this.offsetGyroX = 0f
                            this.offsetGyroY = 0f
                            this.offsetGyroZ = 0f
                            this.offsetAccX = 0f
                            this.offsetAccY = 0f
                            this.offsetAccZ = 0f
                            this._offsetGyroX = 0f
                            this._offsetGyroY = 0f
                            this._offsetGyroZ = 0f
                            this._offsetAccX = 0f
                            this._offsetAccY = 0f
                            this._offsetAccZ = 0f

                            while (this.running.get() && !clientSocket.isClosed) {
                                val readBytes = inputStream!!.read(packetBuffer)
                                require(readBytes == packetBuffer.size) { "Read $readBytes bytes, expected ${packetBuffer.size}" }
                                val packet = GlovePacket.constructFromBytes(packetBuffer)
                                require(packet.packetId == packetId) { "PacketId should be $packetId but is ${packet.packetId}" }
                                handleMPU(packet, i++)
//                                if (i % 100 == 0) {
//                                    println(packet)
//                                    println("Read $i packets, every ${(System.currentTimeMillis() - startTime) / i}ms")
//                                }
                                this.state = packet
                                packetId++
                            }
                        }
                } catch (e: Exception) {
                    this.logger.warn("Connection failed", e)
                }
            }
        }
    }

    private var _offsetGyroX: Float = 0f
    private var _offsetGyroY: Float = 0f
    private var _offsetGyroZ: Float = 0f

    private var _offsetAccX: Float = 0f
    private var _offsetAccY: Float = 0f
    private var _offsetAccZ: Float = 0f

    private var offsetGyroX: Float = 0f
    private var offsetGyroY: Float = 0f
    private var offsetGyroZ: Float = 0f

    private var offsetAccX: Float = 0f
    private var offsetAccY: Float = 0f
    private var offsetAccZ: Float = 0f

    private var lastAngleX: Float = 0f
    private var lastAngleY: Float = 0f
    private var lastAngleZ: Float = 0f

    private var lastTimeConstant: Int = 0

    private fun handleMPU(packet: GlovePacket, num: Int) {
        packet.process(
            this.offsetGyroX,
            this.offsetGyroY,
            this.offsetGyroZ,
            this.offsetAccX,
            this.offsetAccY,
            this.offsetAccZ,
            this.lastAngleX,
            this.lastAngleY,
            this.lastAngleZ,
            this.lastTimeConstant,
        )
        this.lastAngleX = packet.angleX
        this.lastAngleY = packet.angleY
        this.lastAngleZ = packet.angleZ
        this.lastTimeConstant = packet.timeConstant

        if (num < 500) {
            calcMPUOffsets(packet)
        } else if (num == 500) {
            this.offsetGyroX = this._offsetGyroX / 500
            this.offsetGyroY = this._offsetGyroY / 500
            this.offsetGyroZ = this._offsetGyroZ / 500
            this.offsetAccX = this._offsetAccX / 500
            this.offsetAccY = this._offsetAccY / 500
            this.offsetAccZ = this._offsetAccZ / 500
        }
    }

    private fun calcMPUOffsets(packet: GlovePacket) {
        this._offsetGyroX += packet.gyroX
        this._offsetGyroY += packet.gyroY
        this._offsetGyroZ += packet.gyroZ
        this._offsetAccX += packet.accX
        this._offsetAccY += packet.accY
        this._offsetAccZ += packet.accZ
    }
}
