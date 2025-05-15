package avatar.hardware.parts

import avatar.hardware.parts.basecomponents.I2CDevice
import avatar.hardware.parts.basecomponents.PositionSensor
import brain.data.local.Configuration
import brain.data.local.Position
import brain.utils.toI2CDeviceConfiguration
import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import kotlinx.coroutines.Job

class MPU6050(pi4j: Context, positionSensorConfig: Configuration.PositionSensorConfig): PositionSensor, I2CDevice(
    pi4j = pi4j, i2CDeviceConfiguration = positionSensorConfig.toI2CDeviceConfiguration()
) {

    //Value used for the DLPF config.
    private var dlpfCfg: Int = positionSensorConfig.smplrtDiv ?: 0
    //Value used for the sample rate divider.
    private var smplrtDiv: Int = positionSensorConfig.smplrtDiv ?: 0
    //Sensitivity of the measures from the accelerometer. Used to convert accelerometer values.
    private var accelLSBSensitivity: Double = 0.0
    //Sensitivity of the measures from the gyroscope. Used to convert gyroscope values to degrees/sec.
    private var gyroLSBSensitivity: Double = 0.0

    private var updatingJob: Job? = null
    private var updatingThreadStopped: Boolean = true
    private var lastUpdateTime: Long = 0

    // ACCELEROMETER
    //Last acceleration value, in g, retrieved from the accelerometer, for the X axis. (using updatingJob)
    private var accelAccelerationX: Double = 0.0
    //Last acceleration value, in g, retrieved from the accelerometer, for the Y axis. (using updatingJob)
    private var accelAccelerationY: Double = 0.0
    //Last acceleration value, in g, retrieved from the accelerometer, for the Z axis. (using updatingJob)
    private var accelAccelerationZ: Double = 0.0
    //Last angle value, in °, retrieved from the accelerometer, for the X axis. (using updatingJob)
    private var accelAngleX: Double = 0.0
    //Last angle value, in °, retrieved from the accelerometer, for the Y axis. (using updatingJob)
    private var accelAngleY: Double = 0.0
    //Last angle value, in °, retrieved from the accelerometer, for the Z axis. (using updatingJob)
    private var accelAngleZ: Double = 0.0

    //GYROSCOPE
    //Last angular speed value, in °/sec, retrieved from the gyroscope, for the X axis. (using updatingJob)
    private var gyroAngularSpeedX: Double = 0.0
    //Last angular speed value, in °/sec, retrieved from the gyroscope, for the Y axis. (using updatingJob)
    private var gyroAngularSpeedY: Double = 0.0
    //Last angular speed value, in °/sec, retrieved from the gyroscope, for the Z axis. (using updatingJob)
    private var gyroAngularSpeedZ: Double = 0.0
    //Last angle value, in °, calculated from the gyroscope, for the X axis. (using updatingJob)
    private var gyroAngleX: Double = 0.0
    //Last angle value, in °, calculated from the gyroscope, for the Y axis. (using updatingJob)
    private var gyroAngleY: Double = 0.0
    //Last angle value, in °, calculated from the gyroscope, for the Z axis. (using updatingJob)
    private var gyroAngleZ: Double = 0.0
    //Calculated offset for the angular speed from the gyroscope, for the X axis.
    private var gyroAngularSpeedOffsetX: Double = 0.0
    //Calculated offset for the angular speed from the gyroscope, for the Y axis.
    private var gyroAngularSpeedOffsetY: Double = 0.0
    //Calculated offset for the angular speed from the gyroscope, for the Z axis.
    private var gyroAngularSpeedOffsetZ: Double = 0.0

    //FILTERED
    //Last angle value, in °, calculated from the accelerometer and the gyroscope, for the X axis. (using updatingJob)
    private var filteredAngleX: Double = 0.0
    //Last angle value, in °, calculated from the accelerometer and the gyroscope, for the Y axis. (using updatingJob)
    private var filteredAngleY: Double = 0.0
    //Last angle value, in °, calculated from the accelerometer and the gyroscope, for the Z axis. (using updatingJob)
    private var filteredAngleZ: Double = 0.0



    override fun initDevice(i2C: I2C) {
        // 1. waking up the MPU6050 (0x00 = 0000 0000) as it starts in sleep mode.
        updateRegisterValue(MPU6050_REG_ADDR_PWR_MGMT_1, 0x00);

        //TODO init steps. Finished on line #535





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

    /* -----------------------------------------------------------------------
    *                              UTILS
    * -----------------------------------------------------------------------*/


    /**
     * This method updates the value of a specific register with a specific value.
     * The method also checks that the update was successfull.
     * @param address the address of the register to update.
     * @param value the new value to set in the register.
     */
    fun updateRegisterValue(address: Int, value: Int) {
        writeRegister(address, value)

        // we check that the value of the register has been updated
        val readRegisterValue: Int = readRegister(address)

        if (readRegisterValue != value) throw Exception(
            "Error when updating the MPU6050 register value (register: " +
                    address + ", value: " + value + ")"
        )
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
        const val MPU6050_REG_ADDR_GYRO_XOUT_L = 0x44
        const val MPU6050_REG_ADDR_GYRO_YOUT_H = 0x45
        const val MPU6050_REG_ADDR_GYRO_YOUT_L = 0x46
        const val MPU6050_REG_ADDR_GYRO_ZOUT_H = 0x47
        const val MPU6050_REG_ADDR_GYRO_ZOUT_L = 0x48

        //endregion

    }
}