### Kotlin Gpio project. Working with IO lines on Raspberry Pi using Pi4J Kotlin/Java langs and remote compiling / debugging to any ARM GPIO compatible hardware. Advanced AI features (TensorFlow)


[All tutorails and videos on my YouTube channel](https://www.youtube.com/@OleksandrNeiko)



## LESSON 11: i2c LCD 1602 Display.

[I2C LCD 1602 Documentation and i2c address detection](https://docs.sunfounder.com/projects/umsk/en/latest/01_components_basic/26-component_i2c_lcd1602.html)

[PCF8574 Remote 8-Bit I/O Expander for I2C Bus](https://www.ti.com/lit/ds/symlink/pcf8574.pdf?ts=1627006546204)

[Specification for LCD Module 1602A-1(V1.2)](https://www.openhacks.com/uploadsproductos/eone-1602a1.pdf)

#### * To detect i2c device address use those terminal commands on your Raspberry Pi
````
sudo apt-get install i2c-tools
 
i2cdetect -y 1
````

#### Step 1: Add linux-fs pi4j i2c maven Dependency

````
  <dependency>
            <groupId>com.pi4j</groupId>
            <artifactId>pi4j-plugin-linuxfs</artifactId>
            <version>${pi4j.version}</version>
        </dependency>
````


#### Step 2: Add i2c LCD 1602 Display to json config + add to local Configuration data class

````
 "displays": [
    {
      "name": "LCD 1602 Display connected to I2C bus",
      "hardwareModel": "LCD1602",
      "hardwareVersion": "i2c",
      "connectionType": "i2c",
      "pinSDA": 1,
      "pinSCL": 2,
      "addressHexAsString": "0x27",
      "resolutionRows": 2,
      "resolutionColumns": 16
    }
  ]
````

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
    val servos: List<ServoConfig?>? = null,
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
        val connectionType: String?,
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
        val pinSDA: Int?,
        val pinSCL: Int?,
        val addressHexAsString: String?,
        val resolutionRows: Int?,
        val resolutionColumns: Int?,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ServoConfig(
        val name: String?,
        val hardwareModel: String?,
        val hardwareVersion: String?,
        val pin: Int?,
        val installedServoPosition: Int?
    )
}
````

#### Step 3: Create I2CDevice Interface as abstract class, I2CDeviceConfiguration data class and implement a specific type of LCD1602 Display 

````
package avatar.hardware.parts.basecomponents


import brain.data.I2CDeviceConfiguration
import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import java.time.Duration

abstract class I2CDevice(pi4j: Context, i2CDeviceConfiguration: I2CDeviceConfiguration) {

    private var i2c: I2C = pi4j.create(
        I2C.newConfigBuilder(pi4j)
            .id("I2C- ${i2CDeviceConfiguration.hardwareModel} BUS- ${i2CDeviceConfiguration.address}")
            .name(i2CDeviceConfiguration.name)
            .bus(i2CDeviceConfiguration.pinSDA)
            .device(i2CDeviceConfiguration.address)
            .build()
    )

    init {
        this.initDevice(this.i2c)
    }

    abstract fun initDevice(i2C: I2C)

    abstract fun reset()

    protected fun sendCommand(cmd: Byte) {
        i2c.write(cmd)
        delay(Duration.ofNanos(100000))
    }

    protected fun readRegister(register: Int): Int {
        return i2c.readRegisterWord(register)
    }

    protected fun writeRegister(register: Int, config: Int) {
        i2c.writeRegisterWord(register, config)
    }

    protected fun write(data: Byte) {
        i2c.write(data)
    }

    protected fun sendCommand(command: Byte, data: Byte) {
        sendCommand((command.toInt() or data.toInt()).toByte())
    }


    protected fun delay(duration: Duration){
        try {
            val nanos = duration.toNanos()
            val millis = nanos / 1000000
            val remainingNanos = (nanos % 1000000).toInt()
            Thread.sleep(millis, remainingNanos)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

}
````

````
package brain.data

data class I2CDeviceConfiguration(
    val name: String?,
    val hardwareModel: String?,
    val hardwareVersion: String?,
    val pinSDA: Int,
    val pinSCL: Int,
    val address: Int
)
````

````
package avatar.hardware.parts

import avatar.hardware.parts.basecomponents.Display
import avatar.hardware.parts.basecomponents.I2CDevice
import brain.data.Configuration
import brain.utils.toI2CDeviceConfiguration
import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import java.time.Duration
import kotlin.experimental.inv

class DisplayLCD1602(pi4j: Context, displayConfig: Configuration.DisplayConfig): Display, I2CDevice(
    pi4j = pi4j, i2CDeviceConfiguration = displayConfig.toI2CDeviceConfiguration()
)  {

    private val columns = displayConfig.resolutionColumns ?: DEFAULT_COLUMNS
    private val rows = displayConfig.resolutionRows ?: DEFAULT_ROWS
    private var backlight = false


    override fun outputPrint(outFloat: Float?, string: String?, printTimeInMillis: Int?): Boolean {
        displayText(outFloat?.toString() ?: string.toString())
        return true
    }

    override fun initDevice(i2C: I2C) {
        sendLcdTwoPartsCommand(0x03.toByte())
        sendLcdTwoPartsCommand(0x03.toByte())
        sendLcdTwoPartsCommand(0x03.toByte())
        sendLcdTwoPartsCommand(0x02.toByte())

        // Initialize display settings
        sendLcdTwoPartsCommand((LCD_FUNCTION_SET.toInt() or LCD_2LINE.toInt() or LCD_5x8DOTS.toInt() or LCD_4BIT_MODE.toInt()).toByte())
        sendLcdTwoPartsCommand((LCD_DISPLAY_CONTROL.toInt() or LCD_DISPLAY_ON.toInt() or LCD_CURSOR_OFF.toInt() or LCD_BLINK_OFF.toInt()).toByte())
        sendLcdTwoPartsCommand((LCD_ENTRY_MODE_SET.toInt() or LCD_ENTRY_LEFT.toInt() or LCD_ENTRY_SHIFT_DECREMENT.toInt()).toByte())

        clearDisplay()

        // Enable backlight
        setDisplayBacklight(true)
    }

    override fun reset() {
        clearDisplay()
        off()
    }

    fun setDisplayBacklight(backlightEnabled: Boolean) {
        backlight = backlightEnabled
        sendCommand(if (backlight) LCD_BACKLIGHT else LCD_NO_BACKLIGHT)
    }

    fun clearDisplay() {
        moveCursorHome()
        sendLcdTwoPartsCommand(LCD_CLEAR_DISPLAY)
    }

    fun moveCursorHome() {
        sendLcdTwoPartsCommand(LCD_RETURN_HOME)
    }

    fun off() {
        sendCommand(LCD_DISPLAY_OFF)
    }

    fun centerTextInLine(text: String, line: Int) {
        displayLineOfText(text, line, ((columns - text.length) * 0.5).toInt())
    }

    @JvmOverloads
    fun displayLineOfText(text: String, line: Int, position: Int = 0) {
        var text = text
        if (text.length + position > columns) {

            text = text.substring(0, (columns - position))
        }

        if (line > rows || line < 0) {
            //throw exception || logging
        } else {
            setCursorToPosition(line, 0)
            for (i in 0 until position) {
                writeCharacter(' ')
            }
            for (character in text.toCharArray()) {
                writeCharacter(character)
            }
            for (i in 0 until columns - text.length - position) {
                writeCharacter(' ')
            }
        }
    }

    fun displayText(text: String) {
        var currentLine = 0

        val texts = arrayOfNulls<StringBuilder>(rows)
        for (j in 0 until rows) {
            texts[j] = StringBuilder(rows)
        }

        var i = 0
        while (i < text.length) {
            if (currentLine > rows - 1) {
                break
            }
            if (text[i] == '\n') {
                currentLine++
                i++
                continue
            } else if (texts[currentLine]!!.length >= columns) {
                currentLine++
                if (text[i] == ' ') {
                    i++
                }
            }
            // append character to line
            if (currentLine < rows) {
                texts[currentLine]!!.append(text[i])
            }
            i++
        }

        //display the created texts
        for (j in 0 until rows) {
            displayLineOfText(texts[j].toString(), j)
        }
    }

    fun writeCharacter(character: Char) {
        sendLcdTwoPartsCommand(character.code.toByte(), Rs)
    }

    fun writeCharacter(character: Char, line: Int, pos: Int) {
        setCursorToPosition(line, pos)
        sendLcdTwoPartsCommand(character.code.toByte(), Rs)
    }

    private fun displayLine(text: String, pos: Int) {
        sendLcdTwoPartsCommand((0x80 + pos).toByte())

        for (i in 0 until text.length) {
            writeCharacter(text[i])
        }
    }

    fun clearLine(line: Int) {
        if (!(line > rows || line < 1)) {
            displayLine(" ".repeat(columns), LCD_ROW_OFFSETS[line - 1].toInt())
        } else {
            //add exception case || logging
        }

    }

    fun setCursorToPosition(line: Int, pos: Int) {
        if (!(line > rows - 1 || line < 0) && !(pos < 0 || pos > columns - 1)) {
            sendLcdTwoPartsCommand((LCD_SET_DDRAM_ADDR.toInt() or pos + LCD_ROW_OFFSETS[line]).toByte())
        } else {
            //add exception case || logging
        }
    }

    fun createCharacter(location: Int, character: ByteArray) {
        if (character.size == 8 && !(location > 7 || location < 1)) {
            sendLcdTwoPartsCommand((LCD_SET_CGRAM_ADDR.toInt() or (location shl 3)).toByte())

            for (i in 0..7) {
                sendLcdTwoPartsCommand(character[i], 1.toByte())
            }
        }
    }

    fun scrollRight() {
        sendLcdTwoPartsCommand(LCD_SCROLL_RIGHT)
    }

    fun scrollLeft() {
        sendLcdTwoPartsCommand(LCD_SCROLL_LEFT)
    }

    private fun sendLcdTwoPartsCommand(cmd: Byte, mode: Byte = 0.toByte()) {
        //bitwise AND with 11110000 to remove last 4 bits
        writeFourBits((mode.toInt() or (cmd.toInt() and 0xF0)).toByte())
        //bitshift and bitwise AND to remove first 4 bits
        writeFourBits((mode.toInt() or ((cmd.toInt() shl 4) and 0xF0)).toByte())
    }

    private fun writeFourBits(data: Byte) {
        val backlightStatus = if (backlight) LCD_BACKLIGHT else LCD_NO_BACKLIGHT

        write((data.toInt() or En.toInt() or backlightStatus.toInt()).toByte())
        write(((data.toInt() and En.inv().toInt()) or backlightStatus.toInt()).toByte())

        delay(Duration.ofNanos(50000))
    }

    companion object {
        /** Flags for display commands  */
        private const val LCD_CLEAR_DISPLAY = 0x01.toByte()
        private const val LCD_RETURN_HOME = 0x02.toByte()
        private const val LCD_SCROLL_RIGHT = 0x1E.toByte()
        private const val LCD_SCROLL_LEFT = 0x18.toByte()
        private const val LCD_ENTRY_MODE_SET = 0x04.toByte()
        private const val LCD_DISPLAY_CONTROL = 0x08.toByte()
        private const val LCD_CURSOR_SHIFT = 0x10.toByte()
        private const val LCD_FUNCTION_SET = 0x20.toByte()
        private const val LCD_SET_CGRAM_ADDR = 0x40.toByte()
        private const val LCD_SET_DDRAM_ADDR = 0x80.toByte()

        // flags for display entry mode
        private const val LCD_ENTRY_RIGHT = 0x00.toByte()
        private const val LCD_ENTRY_LEFT = 0x02.toByte()
        private const val LCD_ENTRY_SHIFT_INCREMENT = 0x01.toByte()
        private const val LCD_ENTRY_SHIFT_DECREMENT = 0x00.toByte()

        // flags for display on/off control
        private const val LCD_DISPLAY_ON = 0x04.toByte()
        private const val LCD_DISPLAY_OFF = 0x00.toByte()
        private const val LCD_CURSOR_ON = 0x02.toByte()
        private const val LCD_CURSOR_OFF = 0x00.toByte()
        private const val LCD_BLINK_ON = 0x01.toByte()
        private const val LCD_BLINK_OFF = 0x00.toByte()

        // flags for display/cursor shift
        private const val LCD_DISPLAY_MOVE = 0x08.toByte()
        private const val LCD_CURSOR_MOVE = 0x00.toByte()

        // flags for function set
        private const val LCD_8BIT_MODE = 0x10.toByte()
        private const val LCD_4BIT_MODE = 0x00.toByte()
        private const val LCD_2LINE = 0x08.toByte()
        private const val LCD_1LINE = 0x00.toByte()
        private const val LCD_5x10DOTS = 0x04.toByte()
        private const val LCD_5x8DOTS = 0x00.toByte()

        // flags for backlight control
        private const val LCD_BACKLIGHT = 0x08.toByte()
        private const val LCD_NO_BACKLIGHT = 0x00.toByte()
        private const val En = 4.toByte() // Enable bit
        private const val Rw = 2.toByte() // Read/Write bit
        private const val Rs = 1.toByte() // Register select bit

        /**
         * Display row offsets. Offset for up to 4 rows.
         */
        private val LCD_ROW_OFFSETS = byteArrayOf(0x00, 0x40, 0x14, 0x54)
        private const val DEFAULT_DEVICE = "0x27"
        private const val DEFAULT_COLUMNS = 16
        private const val DEFAULT_ROWS = 2
    }

}
````

#### Step 3.1: add DisplayConfiguration to I2CDeviceConfiguration fun

````
package brain.utils

import brain.data.Configuration
import brain.data.I2CDeviceConfiguration
import runtime.setup.Settings.DEFAULT_SCL_PIN
import runtime.setup.Settings.DEFAULT_SDA_PIN

fun Configuration.DisplayConfig.toI2CDeviceConfiguration() = I2CDeviceConfiguration(
        name = this.name,
        hardwareModel = this.hardwareModel,
        hardwareVersion = this.hardwareVersion,
        pinSDA = this.pinSDA ?: DEFAULT_SDA_PIN,
        pinSCL = this.pinSCL ?: DEFAULT_SCL_PIN,
        address = Integer.decode(this.addressHexAsString)
    )

````


#### Step 4: Add parsing to a specific type of LCD1602 i2c display to 'Display' interface. Validate configuration in CircuitBoardImpl

````
package avatar.hardware.parts.basecomponents

import brain.data.Configuration
import brain.utils.filteredHardwareModel

interface Display {

    fun outputPrint(outFloat: Float? = null, string: String? = null, printTimeInMillis: Int? = 0): Boolean

    companion object {
        const val NAME_HARDWARE_MODEL_3461BS_1 = "3461BS-1"
        const val NAME_HARDWARE_MODEL_LCD_1602 = "LCD1602"

        fun isConfigurationValid(displayConfig: Configuration.DisplayConfig): String {

            return if (displayConfig.hardwareModel?.filteredHardwareModel()
                    ?.contains(NAME_HARDWARE_MODEL_3461BS_1.filteredHardwareModel()) == true) {
                NAME_HARDWARE_MODEL_3461BS_1
            } else if (displayConfig.hardwareModel?.filteredHardwareModel()
                    ?.contains(NAME_HARDWARE_MODEL_LCD_1602.filteredHardwareModel()) == true) {
                NAME_HARDWARE_MODEL_LCD_1602
            } else ""
        }

    }


}
````


````
    private fun initHardware() {
    
    /** init displays */
        configuration.displays?.forEach {
            if (it?.hardwareModel != null) {
                when (Display.isConfigurationValid(it)) {
                    Display.NAME_HARDWARE_MODEL_3461BS_1 ->
                        body.displays.add(Display3461BS1(pi4J, it))
                    Display.NAME_HARDWARE_MODEL_LCD_1602 ->
                        if (it.connectionType?.lowercase() == CONNECTION_TYPE_I2C)
                        body.displays.add(DisplayLCD1602(pi4J, it))
                }
            }
        }
    
````

#### Step 5: Main thread logic. i2c display output

````
     (avatar.body as CircuitBoard).displayPrint(string = "Press the button to get weather forecast")
     
     (avatar.body as CircuitBoard).displayPrint(string = "The temperature in $city ${temp.toString()} C")
     
````

#### * Additional settings: remote compiling / debugging setup


Add new launch configuration to IntelliJ IDEA

![screenshot](readme/readme01.png)


fill IP / port adress to Raspberry PI. Username & password as sudo connection. Add Main Kotlin class and project module.

![screenshot](readme/readme02.png)
