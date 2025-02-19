package avatar.hardware.parts

import com.pi4j.io.gpio.digital.DigitalInput
import com.pi4j.io.gpio.digital.DigitalOutput
import kotlinx.coroutines.Job
import brain.data.Configuration

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
        const val NAME_HARDWARE_VERSION_HC_SR_04 = "2021"
        //add other sensors types

        fun isConfigurationValid(sensorConfig: Configuration.DistanceSensorConfig): String {

            if (sensorConfig.hardwareModel
                    ?.filterNot { it == ' ' || it == '-' ||  it == '_' ||  it == ',' ||  it == '.'}?.lowercase()
                    ?.contains(NAME_HARDWARE_MODEL_HC_SR_04.filterNot { it == ' ' || it ==  '-' ||  it == '_' ||  it == ',' ||  it == '.'}
                        .lowercase()) == true) {
                return NAME_HARDWARE_MODEL_HC_SR_04
            } else if (false) {
                //add other types parsing
                return  ""
            } else return ""
        }
    }

}