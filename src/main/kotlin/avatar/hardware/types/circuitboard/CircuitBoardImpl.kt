package avatar.hardware.types.circuitboard

import avatar.hardware.parts.Button
import avatar.hardware.parts.Buzzer
import avatar.hardware.parts.Led
import avatar.hardware.types.circuitboard.data.BodyCircuitBoard
import com.pi4j.context.Context
import kotlinx.coroutines.*
import runtime.setup.Configuration

class CircuitBoardImpl(private val pi4J: Context, private val configuration: Configuration): CircuitBoard {

    override var body: BodyCircuitBoard = BodyCircuitBoard()
        private set

    init {
        initHardware()
    }

    private fun initHardware() {

        //PARSE CONFIG

        /** init Leds */
        configuration.leds?.forEach {
            if (it?.pin != null) {
                body.leds.add(Led(pi4J, it))
            }
        }

        /** init Buttons */
        configuration.buttons?.forEach {
            if (it?.pin != null) {
                body.buttons.add(Button(pi4J, it))
            }
        }

        /** init Buttons */
        configuration.buzzers?.forEach {
            if (it?.pin != null) {
                body.buzzers.add(Buzzer(pi4J, it))
            }
        }
    }


    override fun getLedsCount(): Int = body.leds.size

    override fun ledOn(ledPosition: Int, durationInMillis: Long): Boolean {
        if (ledPosition < 0 || ledPosition >= body.leds.size) return false
        /** multithreading job */
        body.leds[ledPosition].threadScope?.cancel()
        body.leds[ledPosition].threadScope = CoroutineScope(Job() + Dispatchers.IO).launch {
            if (ledPosition < body.leds.size) {
                body.leds[ledPosition].on()

                if (durationInMillis > 0L) {
                    delay(durationInMillis)
                    body.leds[ledPosition].off()
                }
            }
            this.cancel()
        }
        return true
    }

    override fun ledOff(ledPosition: Int): Boolean {
        if (ledPosition < 0 || ledPosition >= body.leds.size) return false
        if (ledPosition < body.leds.size) {
            body.leds[ledPosition].off()
            return true
        }
        return false
    }

    override fun addButtonListeners(buttonPosition: Int, actionHigh: () -> Unit, actionLow: () -> Unit): Boolean {
        if (buttonPosition < 0) return false

        if (buttonPosition < body.buttons.size) {
            body.buttons[buttonPosition].addButtonListeners(actionHigh, actionLow)
            return true
        }
        return false
    }

    override fun buzzerSoundOn(buzzerPosition: Int, durationInMillis: Long): Boolean {
        if (buzzerPosition < 0 || buzzerPosition >= body.buzzers.size) return false
        body.buzzers[buzzerPosition].threadScope?.cancel()
        body.buzzers[buzzerPosition].threadScope = CoroutineScope(Job() + Dispatchers.IO).launch {
            if (buzzerPosition < body.buzzers.size) {
                body.buzzers[buzzerPosition].soundOn()

                if (durationInMillis != 0L) {
                    delay(durationInMillis)
                    body.buzzers[buzzerPosition].soundOff()
                }
            }
            this.cancel()
        }
        return true
    }

    override fun buzzerSoundOff(buzzerPosition: Int): Boolean {
        if (buzzerPosition < 0) return false

        if (buzzerPosition < body.buzzers.size) {
            body.buzzers[buzzerPosition].threadScope?.cancel()
            body.buzzers[buzzerPosition].soundOff()
            return true
        }
        return false
    }

    override fun getBatteryStatus(): Int {
        return 0
    }

}