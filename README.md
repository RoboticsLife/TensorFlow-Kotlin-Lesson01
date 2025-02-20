### Kotlin Gpio project. Working with IO lines on Raspberry Pi using Pi4J Kotlin/Java langs and remote compiling / debugging to any ARM GPIO compatible hardware. Advanced AI features (TensorFlow)


[All tutorails and videos on my YouTube channel](https://www.youtube.com/@OleksandrNeiko)



## LESSON 08: HC-SR04 Ultrasonic Distance Sensor


#### Step 1: Add HC-SR04 Ultrasonic Distance Sensor type of device's part to Json configuration

````
  "distanceSensors": [
    {
      "name": "distance-sensor-HC-SR04",
      "hardwareModel": "HC-SR04",
      "hardwareVersion": "2021",
      "pinTrigger": 25,
      "pinEcho": 24,
      "installedSensorPosition": 0,
      "movingAngle": 0
    }
  ]
````


#### Step 2: Add sensor type to local data class

````
package brain.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Configuration(
    val configName: String? = null,
    val configDescription: String? = null,
    val configVersion: String? = null,
    val hardwareModel: String? = null,
    val hardwareModelCode: Int? = null,
    val hardwareType: String? = null,
    val hardwareTypeCode: Int? = null,
    val leds: List<LedConfig?>? = null,
    val buttons: List<ButtonConfig?>? = null,
    val buzzers: List<BuzzerConfig?>? = null,
    val distanceSensors: List<DistanceSensorConfig?>? = null,
) {
    data class ButtonConfig(
        val name: String?,
        val pin: Int?,
        val pullResistance: Int?
    )

    data class LedConfig(
        val name: String?,
        val pin: Int?
    )

    data class BuzzerConfig(
        val name: String?,
        val pin: Int?
    )

    data class DistanceSensorConfig(
        val name: String?,
        val hardwareModel: String?,
        val hardwareVersion: String?,
        val pinTrigger: Int?,
        val pinEcho: Int?,
        val installedSensorPosition: Int?,
        val movingAngle: Int? = 0
    )
}
````

#### Step 3: Create Distance Sensor Interface and implement a specific type of it

````
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
````

````
package avatar.hardware.parts

import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalInput
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalState
import com.pi4j.io.gpio.digital.PullResistance
import kotlinx.coroutines.Job
import brain.data.Configuration

class DistanceSensorHcSr04v2021(pi4j: Context, distanceSensorConfig: Configuration.DistanceSensorConfig): DistanceSensor {
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
````

#### Step 4: Add functions to Circuit body type Interface and implement it

````
package avatar.hardware.types.circuitboard.data

import avatar.body.BodyPrototype
import avatar.hardware.HardwareTypes
import avatar.hardware.parts.Button
import avatar.hardware.parts.Buzzer
import avatar.hardware.parts.Led

data class BodyCircuitBoard(
    override var type: HardwareTypes.Type = HardwareTypes.Type.CIRCUIT_BOARD,
    val leds: MutableList<Led> = mutableListOf(),
    val buttons: MutableList<Button> = mutableListOf(),
    val buzzers: MutableList<Buzzer> = mutableListOf(),
    val distanceSensors: MutableList<DistanceSensor> = mutableListOf(),
    //TODO: Add hardware parts if need

): BodyPrototype()
````

````
package avatar.hardware.types.circuitboard

import avatar.hardware.Body

interface CircuitBoard: Body {
    
    fun getLedsCount(): Int

    fun ledOn(ledPosition: Int = 0, durationInMillis: Long = 0L): Boolean

    fun ledOff(ledPosition: Int = 0): Boolean

    fun addButtonListeners(buttonPosition: Int = 0, actionHigh: () -> Unit, actionLow: () -> Unit): Boolean

    fun buzzerSoundOn(buzzerPosition: Int = 0, durationInMillis: Long = 0L): Boolean

    fun buzzerSoundOff(buzzerPosition: Int = 0): Boolean
    
    fun stopDistanceMeasuring(sensorPosition: Int = 0): Boolean

    fun getDistanceMeasuringState(sensorPosition: Int = 0): Boolean

}
````

````
    private fun initHardware() {

        //PARSE CONFIG

        /** init Sensors */
        configuration.distanceSensors?.forEach {
            if (it?.pinTrigger != null && it.pinEcho != null) {
                when (DistanceSensor.isConfigurationValid(it)) {
                    DistanceSensor.NAME_HARDWARE_MODEL_HC_SR_04 ->
                        body.distanceSensors.add(DistanceSensorHcSr04v2021(pi4J, it))
                }
            }
        }
    }
    
    ////////////////
    
       override fun startDistanceMeasuring(sensorPosition: Int, periodInMillis: Long): Boolean {
        if (sensorPosition >= body.distanceSensors.size) return false
        body.distanceSensors[sensorPosition].isActive = true

        body.distanceSensors[sensorPosition].threadScopeSensorRequest?.cancel()
        body.distanceSensors[sensorPosition].threadScopeSensorRequest = CoroutineScope(Job() + Dispatchers.IO).launch {
            /** Loop cycle while sensor is active */
            while (body.distanceSensors[sensorPosition].isActive) {
                body.distanceSensors[sensorPosition].triggerOutputHigh()
                TimeUnit.MICROSECONDS.sleep(10)
                body.distanceSensors[sensorPosition].triggerOutputLow()

                while (body.distanceSensors[sensorPosition].echoInput.isLow) {}
                val echoLowNanoTime = System.nanoTime()
                while (body.distanceSensors[sensorPosition].echoInput.isHigh) {}
                val echoHighNanoTime = System.nanoTime()

                DistanceEmitters.emitDistanceData(
                    Distance(
                        sensorPosition = sensorPosition,
                        echoHighNanoTime = echoHighNanoTime,
                        echoLowNanoTime = echoLowNanoTime
                    )
                )

                delay(periodInMillis)
            }
        }
        return true
    }

    override fun stopDistanceMeasuring(sensorPosition: Int): Boolean {
        body.distanceSensors[sensorPosition].isActive = false
        return true
    }

    override fun getDistanceMeasuringState(sensorPosition: Int): Boolean {
        return if (sensorPosition < body.distanceSensors.size) {
            body.distanceSensors[sensorPosition].isActive
        } else {
            false
        }
    }
    
    
````

#### Step 5: Main thread logic. Start distance sensor measuring and trigger received data events

````
    val jobDistanceCollector = CoroutineScope(Job() + Dispatchers.IO).launch {
        DistanceEmitters.distanceSensor.collect { distance ->
            if (distance.toCm(DistanceSensor.NAME_HARDWARE_MODEL_HC_SR_04) == Float.POSITIVE_INFINITY) {
                println("Distance is out of measuring")
            } else {
                println("Distance = ${distance.toCm()} cm")
            }
        }
    }
    
        if (avatar.type == HardwareTypes.Type.CIRCUIT_BOARD) {

        (avatar.body as CircuitBoard).addButtonListeners(
            buttonPosition = 0,
            actionHigh = {
                (avatar.body as CircuitBoard).buzzerSoundOn(0)
                         },
            actionLow = {
                (avatar.body as CircuitBoard).buzzerSoundOff(0)
                weatherNetworkService.getWeatherByName("toronto")
                if (!(avatar.body as CircuitBoard).getDistanceMeasuringState()) {
                    (avatar.body as CircuitBoard).startDistanceMeasuring(periodInMillis = 1000)
                } else {
                    (avatar.body as CircuitBoard).stopDistanceMeasuring()
                }


            }
        )
    }
````

#### * Additional settings: remote compiling / debugging setup


Add new launch configuration to IntelliJ IDEA

![screenshot](readme/readme01.png)


fill IP / port adress to Raspberry PI. Username & password as sudo connection. Add Main Kotlin class and project module.

![screenshot](readme/readme02.png)
