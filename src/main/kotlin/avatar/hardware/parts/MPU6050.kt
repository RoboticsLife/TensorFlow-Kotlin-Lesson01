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
        calibrateSensor()
        //TODO("Not yet implemented")
    }

    override fun reset() {

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

    private fun calibrateSensor() {
        println("Start calibration")
        //TODO
    }


    companion object {
        //region DEFAULT VALUES

        //Default address of the MPU6050 device.
        const val DEFAULT_MPU6050_ADDRESS: Int = 0x68

        //Default value for the digital low pass filter (DLPF) setting for both gyroscope and accelerometer.
        const val DEFAULT_DLPF_CFG: Int = 0x06

        //Default value for the sample rate divider.
        const val DEFAULT_SMPLRT_DIV: Int = 0x00
        //endregion

        //region REGISTERS ADDRESSES

        //Sample Rate Divider. This register specifies the divider from the gyroscope output rate used to generate
        const val MPU6050_REG_ADDR_SMPRT_DIV: Int = 0x19

        //Configuration. his register configures the external Frame Synchronization (FSYNC) pin sampling and
        //the Digital Low Pass Filter (DLPF) setting for both the gyroscopes and accelerometers.
        const val MPU6050_REG_ADDR_CONFIG: Int = 0x1A

        //Gyroscope Configuration. This register is used to trigger gyroscope self-test
        //and configure the gyroscopes’ full scale range
        const val MPU6050_REG_ADDR_GYRO_CONFIG: Int = 0x1B

        //Accelerometer Configuration. This register is used to trigger accelerometer self test and configure
        //the accelerometer full scale range. This register also configures the Digital High Pass Filter (DHPF).
        const val MPU6050_REG_ADDR_ACCEL_CONFIG: Int = 0x1C

        //Interrupt Enable. This register enables interrupt generation by interrupt sources
        const val MPU6050_REG_ADDR_INT_ENABLE: Int = 0x1A

        //Power Management 1. This register allows the user to configure the power mode and clock source.
        // It also provides a bit for resetting the entire device, and a bit for disabling the temperature sensor.
        const val MPU6050_REG_ADDR_PWR_MGMT_1: Int = 0x6B

        //Power Management 2. This register allows the user to configure the frequency of wake-ups in Accelerometer
        // Only Low Power Mode. This register also allows the user to put individual axes of the accelerometer
        // and gyroscope into standby mode.
        const val MPU6050_REG_ADDR_PWR_MGMT_2: Int = 0x6C

        //Accelerometer Measurements. These registers store the most recent accelerometer measurements.
        // #MPU6050_REG_ADDR_ACCEL_XOUT_L
        // #MPU6050_REG_ADDR_ACCEL_YOUT_H
        // #MPU6050_REG_ADDR_ACCEL_YOUT_L
        // #MPU6050_REG_ADDR_ACCEL_ZOUT_H
        // #MPU6050_REG_ADDR_ACCEL_ZOUT_L
        const val MPU6050_REG_ADDR_ACCEL_XOUT_H: Int = 0x3B
        const val MPU6050_REG_ADDR_ACCEL_XOUT_L: Int = 0x3C
        const val MPU6050_REG_ADDR_ACCEL_YOUT_H: Int = 0x3D
        const val MPU6050_REG_ADDR_ACCEL_YOUT_L: Int = 0x3E
        const val MPU6050_REG_ADDR_ACCEL_ZOUT_H: Int = 0x3F
        const val MPU6050_REG_ADDR_ACCEL_ZOUT_L: Int = 0x40

        //Temperature Measurement. These registers store the most recent temperature sensor measurement.
        const val MPU6050_REG_ADDR_TEMP_OUT_H: Int = 0x41

        //Temperature Measurement. These registers store the most recent temperature sensor measurement.
        const val MPU6050_REG_ADDR_TEMP_OUT_L: Int = 0x42

        //Gyroscope Measurements. These registers store the most recent gyroscope measurements.
        // #MPU6050_REG_ADDR_GYRO_XOUT_L
        // #MPU6050_REG_ADDR_GYRO_YOUT_H
        // #MPU6050_REG_ADDR_GYRO_YOUT_L
        // #MPU6050_REG_ADDR_GYRO_ZOUT_H
        // #MPU6050_REG_ADDR_GYRO_ZOUT_L
        const val MPU6050_REG_ADDR_GYRO_XOUT_H = 0x43
        //endregion

    }
}