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