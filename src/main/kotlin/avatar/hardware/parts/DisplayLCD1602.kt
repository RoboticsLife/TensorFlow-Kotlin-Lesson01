package avatar.hardware.parts

import brain.data.Configuration
import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import com.pi4j.io.i2c.I2CProvider

class DisplayLCD1602(pi4j: Context, displayConfig: Configuration.DisplayConfig): Display {

    private lateinit var i2cDisplay: I2C


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
                .device(I2C_DEFAULT_ADDRESS)
                .build()

            val i2cProvider: I2CProvider = pi4j.provider("linuxfs-i2c")

            i2cDisplay = i2cProvider.create(params)

        } else {
            //alternative connection without i2c bus (not implemented)
            return
        }
    }


    override fun outputPrint(outFloat: Float?, string: String?, printTimeInMillis: Int?): Boolean {
        //TODO

        var register = i2cDisplay.register(0x00)
      //  register.write(0x0D)

        return true
    }

    companion object {
        const val CONNECTION_TYPE_I2C = "i2c"
        const val I2C_DEFAULT_ADDRESS = 0x27
    }
}



/**
    https://github.com/Pi4J/pi4j-example-components/blob/main/src/main/java/com/pi4j/catalog/components/LcdDisplay.java
    https://docs.sunfounder.com/projects/ultimate-sensor-kit/en/latest/components_basic/21-component_i2c_lcd1602.html
    https://github.com/Pi4J/pi4j-examples/blob/master/src/main/java/com/pi4j/example/i2c/I2cDeviceExample.java
    https://pinout.xyz/pinout/i2c#:~:text=I2C%20-%20Inter%20Integrated%20Circuit,a%20pull-up%20might%20interfere.
 */
