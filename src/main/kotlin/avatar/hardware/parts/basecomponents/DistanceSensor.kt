package avatar.hardware.parts.basecomponents

import com.pi4j.io.gpio.digital.DigitalInput
import com.pi4j.io.gpio.digital.DigitalOutput
import kotlinx.coroutines.Job
import brain.data.Configuration
import brain.utils.filteredHardwareModel

interface DistanceSensor {

    var triggerOutput: DigitalOutput
    var echoInput: DigitalInput
    var isActive: Boolean

    var threadScopeSensorRequest: Job?

    fun triggerOutputHigh()

    fun triggerOutputLow()


    //logic for parsing sensor types
    companion object {
        const val NAME_HARDWARE_MODEL_HC_SR_04 = "HC-SR04"
        //add other sensors types

        fun isConfigurationValid(sensorConfig: Configuration.DistanceSensorConfig): String {

            return if (sensorConfig.hardwareModel?.filteredHardwareModel()
                    ?.contains(NAME_HARDWARE_MODEL_HC_SR_04.filteredHardwareModel()) == true) {
                NAME_HARDWARE_MODEL_HC_SR_04
            } else ""
        }
    }

}