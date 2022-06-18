package at.tugraz.user_interfaces_ss22.glove

import at.tugraz.user_interfaces_ss22.BaseService
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent
import com.github.kwhat.jnativehook.mouse.NativeMouseListener

/** Keyboard and mouse input acting as glove input for testing the bot without the glove hardware. */
class FakeGloveController : GloveController, BaseService() {

    private val wKey = GloveKeyListener(NativeKeyEvent.VC_W)
    private val sKey = GloveKeyListener(NativeKeyEvent.VC_S)
    private val aKey = GloveKeyListener(NativeKeyEvent.VC_A)
    private val dKey = GloveKeyListener(NativeKeyEvent.VC_D)
    private val qKey = GloveKeyListener(NativeKeyEvent.VC_Q)
    private val eKey = GloveKeyListener(NativeKeyEvent.VC_E)
    private val shiftKey = GloveKeyListener(NativeKeyEvent.VC_SHIFT)

    private val leftMouse = GloveMouseListener(NativeMouseEvent.BUTTON1)
    private val rightMouse = GloveMouseListener(NativeMouseEvent.BUTTON2)

    private var packetId: Byte = 0

    override val state: GlovePacket
        get() = GlovePacket(
            this.packetId++,
            if (wKey.isDown) 4095 else 0, // getThrottle positive
            if (leftMouse.isDown) 4095 else 0, // holdBoost
            if (shiftKey.isDown) 4095 else 0, // holdHandbrake
            0, // holdUseItem
            if (sKey.isDown) 4095 else 0, // getThrottle negative
            if (wKey.isDown) 0 else if (sKey.isDown) 4095 else 2048, // gyro x
            if (aKey.isDown) 0 else if (dKey.isDown) 4095 else 2048, // gyro y
            if (qKey.isDown) 0 else if (eKey.isDown) 4095 else 2048, // gyro z
            if (rightMouse.isDown) 4095 else 0, // acc x
            if (rightMouse.isDown) 4095 else 0, // acc y
            if (rightMouse.isDown) 4095 else 0, // acc z
            0,
        )

    override fun startService() {
        GlobalScreen.registerNativeHook()
        listOf(wKey, sKey, aKey, dKey, qKey, eKey, shiftKey).forEach { GlobalScreen.addNativeKeyListener(it) }
        listOf(leftMouse, rightMouse).forEach { GlobalScreen.addNativeMouseListener(it) }
    }

    override fun stopService() {
        listOf(wKey, sKey, aKey, dKey, qKey, eKey, shiftKey).forEach { GlobalScreen.removeNativeKeyListener(it) }
        listOf(leftMouse, rightMouse).forEach { GlobalScreen.removeNativeMouseListener(it) }
        GlobalScreen.unregisterNativeHook()
    }

    private class GloveKeyListener(private val listenForKey: Int) : NativeKeyListener {
        var isDown: Boolean = false
            private set

        override fun nativeKeyPressed(nativeEvent: NativeKeyEvent?) {
            if (nativeEvent?.keyCode == this.listenForKey) this.isDown = true
        }

        override fun nativeKeyReleased(nativeEvent: NativeKeyEvent?) {
            if (nativeEvent?.keyCode == this.listenForKey) this.isDown = false
        }
    }

    private class GloveMouseListener(private val listenForButton: Int) : NativeMouseListener {
        var isDown: Boolean = false
            private set

        override fun nativeMousePressed(nativeEvent: NativeMouseEvent?) {
            if (nativeEvent?.button == this.listenForButton) this.isDown = true
        }

        override fun nativeMouseReleased(nativeEvent: NativeMouseEvent?) {
            if (nativeEvent?.button == this.listenForButton) this.isDown = false
        }
    }
}
