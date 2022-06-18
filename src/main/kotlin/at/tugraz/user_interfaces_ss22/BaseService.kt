package at.tugraz.user_interfaces_ss22

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseService : Service {

    final override val logger: Logger = LoggerFactory.getLogger(this::class.simpleName)
    final override val running: AtomicBoolean = AtomicBoolean(false)

    final override fun start() {
        if (this.running.getAndSet(true)) throw IllegalStateException("Service is already running")
        this.logger.debug("Starting service")
        startService()
    }

    final override fun close() {
        stopService()
        this.logger.debug("Stopped service")
        this.running.set(false)
    }

    protected open fun startService() {}
    protected open fun stopService() {}
}
