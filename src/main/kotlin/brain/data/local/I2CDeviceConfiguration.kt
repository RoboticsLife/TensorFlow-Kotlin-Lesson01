package brain.data.local

data class I2CDeviceConfiguration(
    val name: String?,
    val hardwareModel: String?,
    val hardwareVersion: String?,
    val pinSDA: Int,
    val pinSCL: Int,
    val address: Int,
    val additionalSettings: Any? = null
)
