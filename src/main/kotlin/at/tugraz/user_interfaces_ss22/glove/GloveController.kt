package at.tugraz.user_interfaces_ss22.glove

import at.tugraz.user_interfaces_ss22.Service

/** Interface providing the latest state of the glove. */
interface GloveController : Service {
    val state: GlovePacket
}
