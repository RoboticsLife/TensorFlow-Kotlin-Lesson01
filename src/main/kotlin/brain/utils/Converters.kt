package brain.utils

import brain.data.local.Configuration
import brain.data.local.I2CDeviceConfiguration
import brain.data.local.MPU6050SettingsConfiguration
import runtime.setup.Settings.DEFAULT_SCL_PIN
import runtime.setup.Settings.DEFAULT_SDA_PIN

fun Configuration.DisplayConfig.toI2CDeviceConfiguration() = I2CDeviceConfiguration(
        name = this.name,
        hardwareModel = this.hardwareModel,
        hardwareVersion = this.hardwareVersion,
        pinSDA = this.pinSDA ?: DEFAULT_SDA_PIN,
        pinSCL = this.pinSCL ?: DEFAULT_SCL_PIN,
        address = Integer.decode(this.addressHexAsString)
    )

fun Configuration.PositionSensorConfig.toI2CDeviceConfiguration() = I2CDeviceConfiguration(
        name = this.name,
        hardwareModel = this.hardwareModel,
        hardwareVersion = this.hardwareVersion,
        pinSDA = this.pinSDA ?: DEFAULT_SDA_PIN,
        pinSCL = this.pinSCL ?: DEFAULT_SCL_PIN,
        address = Integer.decode(this.addressHexAsString),
        additionalSettings = MPU6050SettingsConfiguration(dlpfCfg = this.dlpfCfg, smplrtDiv = this.smplrtDiv)
)

