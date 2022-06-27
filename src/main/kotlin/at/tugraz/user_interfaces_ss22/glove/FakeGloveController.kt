package at.tugraz.user_interfaces_ss22.glove

import at.tugraz.user_interfaces_ss22.BaseService
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent
import com.github.kwhat.jnativehook.mouse.NativeMouseListener
import rlbot.flat.Rotator

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

    override val state: GloveState
        get() = GloveState(
            if (dKey.isDown) 1f else if (aKey.isDown) -1f else 0f, // getSteer
            if (wKey.isDown) 1f else if (sKey.isDown) -1f else 0f, // getThrottle
            if (sKey.isDown) 1f else if (wKey.isDown) -1f else 0f, // getPitch
            if (dKey.isDown) 1f else if (aKey.isDown) -1f else 0f, // getYaw
            if (eKey.isDown) 1f else if (qKey.isDown) -1f else 0f, // getRoll
            rightMouse.isDown, // holdJump
            leftMouse.isDown, // holdBoost
            shiftKey.isDown, // holdHandbrake
            false, // holdUseItem
        )

    override fun createCar(): GloveController {
        return this
    }

    override fun updateCar(rotation: Rotator?): GloveController {
        return this
    }

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
