package avatar.hardware.parts

import brain.data.Configuration
import brain.utils.filteredHardwareModel

interface Servo {

    fun actuatorServoGetCurrentAngle(): Float

    fun actuatorServoGetAngleRangeLimit(): Float

    fun actuatorServoMoveToAngle(angle: Float = 0f, customMovingTimeInMillis: Int? = 0): Boolean



    companion object {
        const val NAME_HARDWARE_MODEL_SG90 = "SG90"


        fun isConfigurationValid(servoConfig: Configuration.ServoConfig): String {

            return if (servoConfig.hardwareModel?.filteredHardwareModel()
                    ?.contains(NAME_HARDWARE_MODEL_SG90.filteredHardwareModel()) == true) {
                NAME_HARDWARE_MODEL_SG90
            } else ""
        }
    }
}