### Kotlin Gpio project. Working with IO lines on Raspberry Pi using Pi4J Kotlin/Java langs and remote compiling / debugging to any ARM GPIO compatible hardware. Advanced AI features (TensorFlow)


[All tutorails and videos on my YouTube channel](https://www.youtube.com/@OleksandrNeiko)



## LESSON 07: Buzzer sound


#### Step 1: Add new type of device's part to Json configuration

````
 "buzzers": [
    {
      "name": "Simple-Buzzer",
      "pin": 26
    }
  ]
````


#### Step 2: Add buzzer type to local data class

````
package runtime.setup

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
}
````

#### Step 3: Add Buzzer class to Avatar body

````
package avatar.hardware.parts

import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalOutputProvider
import kotlinx.coroutines.Job
import runtime.setup.Configuration

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

}
````

````
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
    
    ////////////////
    
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
    
    
````

#### Step 5: Main thread logic. Trigger BuzzerSoundOn / Off function by event

````
    val weatherNetworkService = WeatherNetworkService()

    if (avatar.type == HardwareTypes.Type.CIRCUIT_BOARD) {

        (avatar.body as CircuitBoard).addButtonListeners(
            buttonPosition = 0,
            actionHigh = {
                (avatar.body as CircuitBoard).buzzerSoundOn(0)
                         },
            actionLow = {
                (avatar.body as CircuitBoard).buzzerSoundOff(0)
                weatherNetworkService.getWeatherByName("toronto")

            }
        )
    }  
````

#### * Additional settings: remote compiling / debugging setup


Add new launch configuration to IntelliJ IDEA

![screenshot](readme/readme01.png)


fill IP / port adress to Raspberry PI. Username & password as sudo connection. Add Main Kotlin class and project module.

![screenshot](readme/readme02.png)
