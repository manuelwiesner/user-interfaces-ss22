package at.tugraz.user_interfaces_ss22.rlbot

import at.tugraz.user_interfaces_ss22.BaseService
import java.io.File

/** Automatically start the rlbot python framework */
class RLBotFrameworkService : BaseService() {

    override fun startService() {
        // TODO: redirect input/output, clean up with stopService() etc.
        ProcessBuilder("python", "run-rlbot.py")
            .directory(File("src/main/resources/")) // TODO: unpack file from jar to run outside of dev env
            .start()
    }
}
