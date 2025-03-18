package avatar.hardware.parts

import avatar.hardware.parts.basecomponents.DistanceSensor
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalInput
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalState
import com.pi4j.io.gpio.digital.PullResistance
import kotlinx.coroutines.Job
import brain.data.local.Configuration

class DistanceSensorHcSr04v2021(pi4j: Context, distanceSensorConfig: Configuration.DistanceSensorConfig):
    DistanceSensor {
    override lateinit var triggerOutput: DigitalOutput
    override lateinit var echoInput: DigitalInput
    override var isActive: Boolean = false
    override var threadScopeSensorRequest: Job? = null

    init {
        buildSensor(pi4j, distanceSensorConfig)
    }

    private fun buildSensor(pi4j: Context, distanceSensorConfig: Configuration.DistanceSensorConfig) {
        if (distanceSensorConfig.pinTrigger != null) setTriggerOutput(pi4j, distanceSensorConfig.pinTrigger) else return
        if (distanceSensorConfig.pinEcho != null) setEchoInput(pi4j, distanceSensorConfig.pinEcho) else return
    }

    private fun setTriggerOutput(pi4j: Context, pin: Int) {
        val params = DigitalOutput.newConfigBuilder(pi4j)
            .id("BCM$pin")
            .name("$NAME_HARDWARE_MODEL ($NAME_HARDWARE_VERSION) #$pin")
            .address(pin)
            .initial(DigitalState.LOW)

        triggerOutput = pi4j.create(params)
    }

    private fun setEchoInput(pi4j: Context, pin: Int) {
        val params = DigitalInput.newConfigBuilder(pi4j)
            .id("BCM$pin")
            .name("$NAME_HARDWARE_MODEL ($NAME_HARDWARE_VERSION) #$pin")
            .address(pin)
            .pull(PullResistance.PULL_DOWN)
            .build()

        echoInput = pi4j.create(params)
    }


    override fun triggerOutputHigh() {
        triggerOutput.setState(1)
    }

    override fun triggerOutputLow() {
        triggerOutput.setState(0)
    }

    companion object {
        const val NAME_HARDWARE_MODEL = "HC-SR04"
        const val NAME_HARDWARE_VERSION = "2021"
    }
}