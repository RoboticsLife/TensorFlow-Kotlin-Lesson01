package avatar.hardware.parts.java

import java.time.Duration
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger

abstract class Component protected constructor() {
    /**
     * Override this method to clean up all used resources
     */
    open fun reset() {
        //nothing to do by default
    }

    protected fun logInfo(msg: String, vararg args: Any?) {
        logger.info { String.format(msg, *args) }
    }

    protected fun logError(msg: String, vararg args: Any?) {
        logger.severe { String.format(msg, *args) }
    }

    protected fun logDebug(msg: String, vararg args: Any?) {
        logger.fine { String.format(msg, *args) }
    }

    protected fun logException(msg: String?, exception: Throwable?) {
        logger.log(Level.SEVERE, msg, exception)
    }

    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An [InterruptedException] will be caught and ignored while setting the interrupt flag again.
     *
     * @param duration Time to sleep
     */
    protected fun delay(duration: Duration) {
        try {
            val nanos = duration.toNanos()
            val millis = nanos / 1000000
            val remainingNanos = (nanos % 1000000).toInt()
            Thread.sleep(millis, remainingNanos)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    protected fun <T> asMock(type: Class<T>, instance: Any?): T {
        return type.cast(instance)
    }

    companion object {
        /**
         * Logger instance
         */
        private val logger: Logger = Logger.getLogger("Pi4J Components")

        init {
            val appropriateLevel = Level.INFO

            //Level appropriateLevel = Level.FINE; //use if 'debug'
            System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%4\$s: %5\$s [%1\$tl:%1\$tM:%1\$tS %1\$Tp]%n"
            )

            logger.level = appropriateLevel
            logger.useParentHandlers = false
            val handler = ConsoleHandler()
            handler.level = appropriateLevel
            logger.addHandler(handler)
        }
    }
}
