package avatar.hardware.parts

import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalOutputProvider
import kotlinx.coroutines.Job
import brain.data.local.Configuration

class Buzzer(pi4j: Context, buzzerConfig: Configuration.BuzzerConfig) {

    private lateinit var buzzerOutput: DigitalOutput
    private lateinit var name: String
    var threadScope: Job? = null

    init {
        buildBuzzer(pi4j, buzzerConfig)
    }

    private fun buildBuzzer(pi4j: Context, buzzerConfig: Configuration.BuzzerConfig) {
        buzzerOutput = pi4j.digitalOutput<DigitalOutputProvider>().create(buzzerConfig.pin)
        name = buzzerConfig.name ?: "BUZZER"
    }

    fun soundOn() {
        buzzerOutput.high()
    }

    fun soundOff() {
        buzzerOutput.low()
    }

}