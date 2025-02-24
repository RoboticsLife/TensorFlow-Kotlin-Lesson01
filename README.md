### Kotlin Gpio project. Working with IO lines on Raspberry Pi using Pi4J Kotlin/Java langs and remote compiling / debugging to any ARM GPIO compatible hardware. Advanced AI features (TensorFlow)


[All tutorails and videos on my YouTube channel](https://www.youtube.com/@OleksandrNeiko)



## LESSON 09: 4-Digits Display 3461BS-1 


#### Step 1: Add 3461BS-1  4-Digits Display type of device's part to Json configuration

````
 "displays": [
    {
      "name": "4 Digits display (7 led sections)",
      "hardwareModel": "3461BS-1",
      "hardwareVersion": "",
      "pin01": 20,
      "pin02": 16,
      "pin03": 12,
      "pin04": 18,
      "pin05": 27,
      "pin06": 17,
      "pin07": 22,
      "pin08": 5,
      "pin09": 6,
      "pin10": 13,
      "pin11": 19,
      "pin12": 23
    }
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
    val displays: List<DisplayConfig?>? = null,
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ButtonConfig(
        val name: String?,
        val pin: Int?,
        val pullResistance: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class LedConfig(
        val name: String?,
        val pin: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class BuzzerConfig(
        val name: String?,
        val pin: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class DistanceSensorConfig(
        val name: String?,
        val hardwareModel: String?,
        val hardwareVersion: String?,
        val pinTrigger: Int?,
        val pinEcho: Int?,
        val installedSensorPosition: Int?,
        val movingAngle: Int? = 0
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class DisplayConfig(
        val name: String?,
        val hardwareModel: String?,
        val hardwareVersion: String?,
        val pin01: Int?,
        val pin02: Int?,
        val pin03: Int?,
        val pin04: Int?,
        val pin05: Int?,
        val pin06: Int?,
        val pin07: Int?,
        val pin08: Int?,
        val pin09: Int?,
        val pin10: Int?,
        val pin11: Int?,
        val pin12: Int?,
    )
}
````

#### Step 3: Create Display Interface and implement a specific type of it for 3461BS-1

````
package avatar.hardware.parts

import brain.data.Configuration

interface Display {

    fun outputPrint(outFloat: Float? = null, string: String? = null, printTimeInMillis: Int? = 0): Boolean

    companion object {
        const val NAME_HARDWARE_MODEL_3461BS_1 = "3461BS-1"


        fun isConfigurationValid(displayConfig: Configuration.DisplayConfig): String {

            if (displayConfig.hardwareModel
                    ?.filterNot { it == ' ' || it == '-' || it == '_' || it == ',' || it == '.' }?.lowercase()
                    ?.contains(NAME_HARDWARE_MODEL_3461BS_1.filterNot { it == ' ' || it == '-' || it == '_' || it == ',' || it == '.' }
                        .lowercase()) == true) {
                return NAME_HARDWARE_MODEL_3461BS_1
            } else if (false) {
                //TODO add other types implementations
                return ""
            } else return ""
        }
    }

}
````

````
package avatar.hardware.parts

import brain.data.Configuration
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalOutputProvider
import kotlinx.coroutines.*

class Display3461BS1(pi4j: Context, displayConfig: Configuration.DisplayConfig): Display {

    //12 output pins
    private lateinit var output01: DigitalOutput
    private lateinit var output02: DigitalOutput
    private lateinit var output03: DigitalOutput
    private lateinit var output04: DigitalOutput
    private lateinit var output05: DigitalOutput
    private lateinit var output06: DigitalOutput
    private lateinit var output07: DigitalOutput
    private lateinit var output08: DigitalOutput
    private lateinit var output09: DigitalOutput
    private lateinit var output10: DigitalOutput
    private lateinit var output11: DigitalOutput
    private lateinit var output12: DigitalOutput

    //digit's cursors
    private lateinit var digitsAddressRegisters: List<DigitalOutput>
    //symbol sector's cursors
    private lateinit var symbolsAddressRegisters: List<DigitalOutput>
    //dot divider cursor
    private lateinit var dotDividerAddressRegister: DigitalOutput

    private var name: String? = null
    private var threadScope: Job? = null


    init {
        buildDisplayRegisters(pi4j, displayConfig)
    }

    private fun buildDisplayRegisters(pi4j: Context, displayConfig: Configuration.DisplayConfig) {
        try {
            output01 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin01)
            output02 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin02)
            output03 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin03)
            output04 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin04)
            output05 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin05)
            output06 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin06)
            output07 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin07)
            output08 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin08)
            output09 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin09)
            output10 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin10)
            output11 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin11)
            output12 = pi4j.digitalOutput<DigitalOutputProvider>().create(displayConfig.pin12)

            name = displayConfig.name

            digitsAddressRegisters = listOf(output12, output09, output08, output06)
            //Symbol parts starting from left bottom part arranged clockwise (inner center part at the end of registers)
            symbolsAddressRegisters = listOf(output01, output10, output11, output07, output04, output02, output05)
            dotDividerAddressRegister = output03
        } catch (_: Exception) {}
    }

    private fun activateDigitCursor(digitPosition: Int) {
        when (digitPosition) {
            0 -> {
                output12.high() //First digit
                output09.low() //Second digit
                output08.low() //Third digit
                output06.low() //Fours digit
            }
            1 -> {
                output12.low() //First digit
                output09.high() //Second digit
                output08.low() //Third digit
                output06.low() //Fours digit
            }
            2 -> {
                output12.low() //First digit
                output09.low() //Second digit
                output08.high() //Third digit
                output06.low() //Fours digit
            }
            3 -> {
                output12.low() //First digit
                output09.low() //Second digit
                output08.low() //Third digit
                output06.high() //Fours digit
            }
        }
    }

    private fun mapASCItoOutputs(symbol: Char): List<Int> {
        return when(symbol) {
            '0', 'o', 'O' -> listOf(0, 0, 0, 0, 0, 0, 1)
            '1', 'i', 'I' -> listOf(1, 1, 1, 0, 0, 1, 1)
            '2' -> listOf(0, 1, 0, 0, 1, 0, 0)
            '3' -> listOf(1, 1, 0, 0, 0, 0, 0)
            '4' -> listOf(1, 0, 1, 0, 0, 1, 0)
            '5' -> listOf(1, 0, 0, 1, 0, 0, 0)
            '6' -> listOf(0, 0, 0, 1, 0, 0, 0)
            '7' -> listOf(1, 1, 0, 0, 0, 1, 1)
            '8' -> listOf(0, 0, 0, 0, 0, 0, 0)
            '9' -> listOf(1, 0, 0, 0, 0, 1, 0)

            'l', 'L' -> listOf(0, 0, 1, 1, 1, 0, 1)
            '_' -> listOf(1, 1, 1, 1, 1, 0, 1)
            '-' -> listOf(1, 1, 1, 1, 1, 1, 0)
            ' ' -> listOf(1, 1, 1, 1, 1, 1, 1)
            else -> listOf(1, 1, 1, 1, 1, 1, 1)
        }
    }


    override fun outputPrint(outFloat: Float?, string: String?, printTimeInMillis: Int?): Boolean {
        var runTileClockMillis = 0

        //detect number divider
        val pointCursorIfPresent = if (outFloat != null) {
            if (outFloat.rem(1).toDouble().equals(0.0)) -1 else outFloat.toString().indexOf(".")
        } else if (!string.isNullOrEmpty()) string.indexOf(".") else -1

        //Prepare incoming data to formatted string
        var outputCharArray = if (outFloat != null && (outFloat.rem(1).toDouble().equals(0.0)))
            outFloat.toInt().toString() else outFloat?.toString()
            ?: if (!string.isNullOrEmpty()) string else ""
        outputCharArray = outputCharArray.replace(".", "")

        if (outputCharArray.length > 4) outputCharArray = outputCharArray.substring(0, 4)

        threadScope?.cancel()

        threadScope = CoroutineScope(Job() + Dispatchers.IO).launch {
            while (if (printTimeInMillis == null || printTimeInMillis == 0) true else printTimeInMillis >= runTileClockMillis) {
                for (i in outputCharArray.indices) {
                    //to print Digit with correct pulse high voltage frequency delay
                    val millis = (16 / outputCharArray.length).toLong()
                    delay(millis)
                    runTileClockMillis += millis.toInt()
                    activateDigitCursor(i) //activate digit cursor
                    mapASCItoOutputs(outputCharArray[i]).forEachIndexed { index, state ->
                        symbolsAddressRegisters[index].setState(state)

                        //print divider if need
                        if (pointCursorIfPresent > 0 && pointCursorIfPresent-1 == i)
                            dotDividerAddressRegister.low() else dotDividerAddressRegister.high()
                    }
                }
            }
            //Erase all data from display (clear procedure)
            for (i in 0..3) {
                activateDigitCursor(i) //activate digit cursor
                symbolsAddressRegisters.forEach { it.high() }
                dotDividerAddressRegister.high()
            }
        }
        return true
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
    val displays: MutableList<Display> = mutableListOf(),
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
    
    fun displayPrint(displayPosition: Int = 0, outFloat: Float? = null, string: String? = null, printTimeInMillis: Int? = 0): Boolean

}
````

````
    private fun initHardware() {

        //PARSE CONFIG

        /** init displays */
        configuration.displays?.forEach {
            if (it?.pin01 != null && it?.pin02 != null) {
                when (Display.isConfigurationValid(it)) {
                    Display.NAME_HARDWARE_MODEL_3461BS_1 ->
                        body.displays.add(Display3461BS1(pi4J, it))
                }
            }
        }
    }
    
    ////////////////
    
      override fun displayPrint(displayPosition: Int, outFloat: Float?, string: String?, printTimeInMillis: Int?): Boolean {
        if (displayPosition < 0) return false

        if (displayPosition < body.displays.size) {
            return body.displays[displayPosition].outputPrint(outFloat, string, printTimeInMillis)
        }
        return false
    }
    
    
````

#### Step 5: Main thread logic. Set initial display print out data as "_LO_" then print the temperature from API response

````

   (avatar.body as CircuitBoard).displayPrint(string = "_LO_",)    
    
   fun collectData() {

    val jobWeatherCollector = CoroutineScope(Job() + Dispatchers.IO).launch {
        NetworkEmitters.weatherEmitter.collect { weather ->
            if (weather.isSuccessful && weather.weatherResponse != null) {
                println(weather)
                val temp = weather.weatherResponse.main?.temp?.toInt()
                (avatar.body as CircuitBoard).displayPrint(string = temp.toString())

                CoroutineScope(Dispatchers.IO).launch {
                    //if temp = 0 -> blink 2 leds once
                    if (temp != null && temp == 0) {
                        (avatar.body as CircuitBoard).ledOn(0, 1000L)
                        (avatar.body as CircuitBoard).ledOn(1, 1000L)
                    } else if (temp != null && temp > 0) {
                        //if temp > 0 -> blink green led $temp times
                        for (i in 1..temp) {
                            (avatar.body as CircuitBoard).ledOn(0, 1000L)
                            delay(2000)
                        }
                    } else if (temp != null && temp < 0) {
                        //if temp > 0 -> blink green led $temp times
                        for (i in 1..abs(temp)) {
                            (avatar.body as CircuitBoard).ledOn(1, 1000L)
                            delay(2000)
                        }
                    }

                    (avatar.body as CircuitBoard).buzzerSoundOn(0, 300)
                    delay(600)
                    (avatar.body as CircuitBoard).buzzerSoundOn(0, 300)
                    delay(600)
                    (avatar.body as CircuitBoard).buzzerSoundOn(0, 300)

                }
            }

        }
    }
}
````

#### * Additional settings: remote compiling / debugging setup


Add new launch configuration to IntelliJ IDEA

![screenshot](readme/readme01.png)


fill IP / port adress to Raspberry PI. Username & password as sudo connection. Add Main Kotlin class and project module.

![screenshot](readme/readme02.png)
