package avatar.hardware.parts

import brain.data.Configuration
import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import com.pi4j.io.i2c.I2CProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.TimeUnit

class DisplayLCD1602(pi4j: Context, displayConfig: Configuration.DisplayConfig): Display {

    private lateinit var i2cDisplay: I2C
    //private var threadScope: Job? = null


    init {
        buildDisplayRegisters(pi4j, displayConfig)
        outputPrint(string = "KU KU")
    }

    private fun buildDisplayRegisters(pi4j: Context, displayConfig: Configuration.DisplayConfig) {
        if (displayConfig.connectionType?.lowercase() == CONNECTION_TYPE_I2C && displayConfig.pinSDA != null && displayConfig.pinSCL != null) {
            val params = I2C.newConfigBuilder(pi4j)
                .id("address: $CONNECTION_TYPE_I2C" + ":_" + displayConfig.hardwareModel + "_" + displayConfig.connectionType)
                .name(displayConfig.name)
                .bus(displayConfig.pinSDA)
            //    .device(I2C_DEFAULT_ADDRESS)
                .device(0x27)
             //   .device(0x3F)
               // .device(0x20)
                .build()

         //   val i2cProvider: I2CProvider = pi4j.provider("linuxfs-i2c")
            val i2cProvider: I2CProvider = pi4j.provider("linuxfs-i2c")

            i2cDisplay = i2cProvider.create(params)

        } else {
            //alternative connection without i2c bus (not implemented)
            return
        }
    }

    private fun sendCommand(command: Byte) {
       CoroutineScope(Job() + Dispatchers.Default).launch {
        //    i2cDisplay.write(0x00)



           //     i2cDisplay.write(0x04)
        //   i2cDisplay.write((1).toByte())
//           i2cDisplay.write(0b000_00100 and 0b000_00100 or 0x08)
           TimeUnit.MICROSECONDS.sleep(100)
       }
    }

    private fun writeFourBits(data: Byte) {

       // i2cDisplay.write((data.toInt() or com.pi4j.catalog.components.LcdDisplay.En.toInt() or backlightStatus.toInt()).toByte())
    //    i2cDisplay.write(data.toInt() and 0b000_00100 or 0x08)
       // i2cDisplay.write((00 or 00))

    }


    override fun outputPrint(outFloat: Float?, string: String?, printTimeInMillis: Int?): Boolean {
        //TODO

        var register = i2cDisplay.register(0x00)

        sendCommand(LCD_DISPLAY_OFF)

        return true
    }

    companion object {
        const val CONNECTION_TYPE_I2C = "i2c"
        const val I2C_DEFAULT_ADDRESS: Byte = 0x27

        // flags for display on/off control
        const val LCD_DISPLAY_ON: Byte = 0x04.toByte()
        const val LCD_DISPLAY_OFF: Byte = 0x00.toByte()
    }
}



/**
    https://github.com/Pi4J/pi4j-example-components/blob/main/src/main/java/com/pi4j/catalog/components/LcdDisplay.java
    https://docs.sunfounder.com/projects/ultimate-sensor-kit/en/latest/components_basic/21-component_i2c_lcd1602.html
    https://github.com/Pi4J/pi4j-examples/blob/master/src/main/java/com/pi4j/example/i2c/I2cDeviceExample.java
    https://pinout.xyz/pinout/i2c#:~:text=I2C%20-%20Inter%20Integrated%20Circuit,a%20pull-up%20might%20interfere.
    https://learn.adafruit.com/scanning-i2c-addresses/raspberry-pi
 */
