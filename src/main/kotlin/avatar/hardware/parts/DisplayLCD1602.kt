package avatar.hardware.parts

import brain.data.Configuration
import com.pi4j.context.Context

class DisplayLCD1602(pi4j: Context, displayConfig: Configuration.DisplayConfig): Display {

    init {
        println("Booom !")
    }


    override fun outputPrint(outFloat: Float?, string: String?, printTimeInMillis: Int?): Boolean {
        //TODO
        return true
    }
}