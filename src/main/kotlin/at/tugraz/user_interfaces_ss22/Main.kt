package at.tugraz.user_interfaces_ss22

import at.tugraz.user_interfaces_ss22.glove.GloveControllerService
import at.tugraz.user_interfaces_ss22.glove.FakeGloveController
import at.tugraz.user_interfaces_ss22.mapping.GloveState
import at.tugraz.user_interfaces_ss22.rlbot.RLBotFrameworkService
import at.tugraz.user_interfaces_ss22.rlbot.RLBotPythonService

fun main(args: Array<String>) {
    if (args.size !in 3..4) return println("Usage: ./gradlew run --args=\"<port_python> <port_glove> <start_framework> [refresh_rate]\"")

    val portPython = args[0].toIntOrNull() ?: return println("Argument <port_python> needs to be an integer.")
    val portGlove = args[1].toIntOrNull() ?: return println("Argument <port_glove> needs to be an integer.")
    val startFramework = args[2].toBooleanStrictOrNull() ?: return println("Argument <start_framework> needs to be a boolean.")
    val refreshRate = (args.getOrNull(3) ?: "120").toIntOrNull() ?: return println("Argument <refresh_rate> needs to be an integer.")

    Runner(portPython, portGlove, startFramework, refreshRate).use { it.start() }
}

class Runner(
    private val portPython: Int,
    private val portGlove: Int,
    private val startFramework: Boolean,
    private val refreshRate: Int,
) : BaseService() {

    private val runningServices: MutableList<Service> = arrayListOf()
    private val runningThreads: MutableList<Thread> = arrayListOf()

    override fun startService() {
        val gloveController = if (this.portGlove == -1) FakeGloveController() else GloveControllerService(this.portGlove)
        startService(gloveController)
        val rlBotInterface = RLBotPythonService(this.portPython, this.refreshRate, { GloveState(gloveController) })
        startService(rlBotInterface)

        if (this.startFramework) {
            startService(RLBotFrameworkService())
        }

        this.runningThreads.forEach { it.join() }
    }

    private fun startService(service: Service) {
        this.runningServices += service
        val thread = Thread(service::start)
        this.runningThreads += thread
        thread.start()
    }

    override fun stopService() {
        this.runningServices.forEach(Service::close)
        this.runningServices.clear()
        Thread.sleep(1000)
        this.runningThreads.forEach {
            if (it.isAlive) {
                this.logger.error("Thread is still alive after 1 second grace: $it")
            }
        }
    }
}
