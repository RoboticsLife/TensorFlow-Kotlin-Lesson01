package avatar.hardware.parts

import avatar.hardware.parts.basecomponents.I2CDevice
import avatar.hardware.parts.basecomponents.PositionSensor
import brain.data.local.Configuration
import brain.data.local.Position
import brain.utils.toI2CDeviceConfiguration
import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C

class MPU6050(pi4j: Context, positionSensorConfig: Configuration.PositionSensorConfig): PositionSensor, I2CDevice(
    pi4j = pi4j, i2CDeviceConfiguration = positionSensorConfig.toI2CDeviceConfiguration()
) {
    override fun initDevice(i2C: I2C) {
        //TODO("Not yet implemented")
    }

    override fun reset() {
        //TODO("Not yet implemented")
    }

    override fun getPositionData(): Position {
        //TODO("Not yet implemented")
        return Position(0, "MPU6050", "BBOON")
    }

    override fun getGyroscopePositionData(): Position {
        TODO("Not yet implemented")
        return Position(0, "MPU6050", "BBOON")
    }

    override fun getGPSPositionData(): Position {
        TODO("Not yet implemented")
        return Position(0, "MPU6050", "BBOON")
    }
}