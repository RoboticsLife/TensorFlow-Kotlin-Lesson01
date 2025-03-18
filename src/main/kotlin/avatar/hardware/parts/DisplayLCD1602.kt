package avatar.hardware.parts

import avatar.hardware.parts.basecomponents.Display
import avatar.hardware.parts.basecomponents.I2CDevice
import brain.data.local.Configuration
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