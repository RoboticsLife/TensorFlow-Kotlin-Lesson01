package avatar.hardware.parts

import brain.data.Configuration

interface Servo {

    fun actuatorServoGetCurrentAngle(): Float

    fun actuatorServoGetAngleRangeLimit(): Float

    fun actuatorServoMoveToAngle(angle: Float = 0f): Boolean



    companion object {
        const val NAME_HARDWARE_MODEL_SG90 = "SG90"


        fun isConfigurationValid(servoConfig: Configuration.ServoConfig): String {

            if (servoConfig.hardwareModel
                    ?.filterNot { it == ' ' || it == '-' || it == '_' || it == ',' || it == '.' }?.lowercase()
                    ?.contains(NAME_HARDWARE_MODEL_SG90.filterNot { it == ' ' || it == '-' || it == '_' || it == ',' || it == '.' }
                        .lowercase()) == true) {
                return NAME_HARDWARE_MODEL_SG90
            } else if (false) {
                //TODO add other types implementations
                return ""
            } else return ""
        }
    }
}