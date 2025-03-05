package brain.data

data class I2CDeviceConfiguration(
    val name: String?,
    val hardwareModel: String?,
    val hardwareVersion: String?,
    val pinSDA: Int,
    val pinSCL: Int,
    val address: Int
)
