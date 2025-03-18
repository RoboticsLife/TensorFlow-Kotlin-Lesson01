package avatar.hardware.parts

import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.*
import brain.data.local.Configuration

class Button(pi4j: Context, buttonConfig: Configuration.ButtonConfig) {

    private lateinit var buttonInput: DigitalInput

    init {
        buildButton(pi4j, buttonConfig)
    }

    private fun buildButton(pi4j: Context, buttonConfig: Configuration.ButtonConfig) {
        val params = DigitalInput.newConfigBuilder(pi4j)
            .id("BCM${buttonConfig.pin}")
            .name(buttonConfig.name)
            .address(buttonConfig.pin)
            .debounce(DigitalInput.DEFAULT_DEBOUNCE)
            .pull(
                when (buttonConfig.pullResistance) {
                    0 -> PullResistance.PULL_DOWN
                    1 -> PullResistance.PULL_UP
                    else -> PullResistance.PULL_DOWN
                }
            )
            .build()

        buttonInput = pi4j.create(params)
    }

    fun addButtonListeners(actionHigh: () -> Unit, actionLow: () -> Unit) {
        buttonInput.addListener(DigitalStateChangeListener { e: DigitalStateChangeEvent<*> ->
            if (e.state() == DigitalState.HIGH) {
                println("${buttonInput.name} was pressed") //TODO add to event emitter
                actionHigh()
            }
            if (e.state() == DigitalState.LOW) {
                println("${buttonInput.name} was unpressed") //TODO add to event emitter
                actionLow()
            }

        })

    }
}