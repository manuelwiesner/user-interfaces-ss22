package at.tugraz.user_interfaces_ss22.glove

/** Interface providing the latest state of the glove. */
interface GloveController : AutoCloseable {
    val state: GlovePacket
}
