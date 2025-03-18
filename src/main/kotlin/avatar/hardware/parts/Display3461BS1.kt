package avatar.hardware.parts

import avatar.hardware.parts.basecomponents.Display
import brain.data.local.Configuration
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