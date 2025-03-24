package avatar.hardware.parts.basecomponents

import brain.data.local.Configuration
import brain.data.local.Position
import brain.utils.filteredHardwareModel

interface PositionSensor {

    fun getPositionData(): Position?

    fun getGyroscopePositionData(): Position?

    fun getGPSPositionData(): Position?

    //TODO add logic

    companion object {
        const val NAME_HARDWARE_MODEL_MPU6050 = "MPU6050"

        fun isConfigurationValid(positionSensorConfig: Configuration.PositionSensorConfig): String {

            return if (positionSensorConfig.hardwareModel?.filteredHardwareModel()
                    ?.contains(NAME_HARDWARE_MODEL_MPU6050.filteredHardwareModel()) == true) {
                NAME_HARDWARE_MODEL_MPU6050
            } else ""
        }

    }
}