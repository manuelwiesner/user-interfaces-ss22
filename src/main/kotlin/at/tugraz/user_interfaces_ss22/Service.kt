package at.tugraz.user_interfaces_ss22

import org.slf4j.Logger
import java.util.concurrent.atomic.AtomicBoolean

interface Service : AutoCloseable {

    /** Logger of this service. */
    val logger: Logger

    /** Indicates whether this service is currently running. */
    val running: AtomicBoolean

    /** Starts this service, may block indefinitely. Throws if already running. */
    fun start()
}
