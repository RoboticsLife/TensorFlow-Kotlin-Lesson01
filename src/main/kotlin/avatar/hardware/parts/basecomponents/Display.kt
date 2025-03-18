package avatar.hardware.parts.basecomponents

import brain.data.local.Configuration
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