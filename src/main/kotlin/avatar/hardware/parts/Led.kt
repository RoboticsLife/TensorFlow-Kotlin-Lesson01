package avatar.hardware.parts

import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalOutputProvider
import kotlinx.coroutines.Job
import runtime.setup.Configuration

class Led(pi4j: Context, ledConfig: Configuration.LedConfig) {

    private lateinit var ledOutput: DigitalOutput
    private lateinit var name: String
    var threadScope: Job? = null

    init {
        buildLed(pi4j, ledConfig)
    }

    private fun buildLed(pi4j: Context, ledConfig: Configuration.LedConfig) {
        ledOutput = pi4j.digitalOutput<DigitalOutputProvider>().create(ledConfig.pin)
        name = ledConfig.name ?: "LED"
    }

    fun on() {
        ledOutput.high()
    }

    fun off() {
        ledOutput.low()
    }

}