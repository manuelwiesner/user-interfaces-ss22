package at.tugraz.user_interfaces_ss22

import at.tugraz.user_interfaces_ss22.glove.GloveControllerESP
import at.tugraz.user_interfaces_ss22.glove.GloveControllerKeyAndMouse
import at.tugraz.user_interfaces_ss22.mapping.GloveState
import at.tugraz.user_interfaces_ss22.rlbot.RLBotPythonInterface

fun main(args: Array<String>) {
    if (args.size !in 2..3) return println("Usage: ./gradlew run --args=\"<port_python> <port_esp> [refresh_rate]\"")

    val portPython = args[0].toIntOrNull() ?: return println("Argument <port_python> needs to be an integer.")
    val portEsp = args[1].toIntOrNull() ?: return println("Argument <port_esp> needs to be an integer.")
    val refreshRate = (args.getOrNull(2) ?: "120").toIntOrNull() ?: return println("Argument <refresh_rate> needs to be an integer.")

    RLBotPythonInterface(portPython, refreshRate) {
        GloveState(if (portEsp == -1) GloveControllerKeyAndMouse() else GloveControllerESP(portEsp))
    }.start()
}
