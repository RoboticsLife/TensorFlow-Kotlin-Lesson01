package avatar.hardware.parts

import avatar.hardware.parts.basecomponents.I2CDevice
import avatar.hardware.parts.basecomponents.PositionSensor
import brain.data.local.Configuration
import brain.data.local.Position
import brain.utils.toI2CDeviceConfiguration
import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import kotlinx.coroutines.*
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class MPU6050basic(pi4j: Context, positionSensorConfig: Configuration.PositionSensorConfig): PositionSensor, I2CDevice(
    pi4j = pi4j, i2CDeviceConfiguration = positionSensorConfig.toI2CDeviceConfiguration()
) {



    lateinit var mpu6050: I2C


    override fun initDevice(i2C: I2C) {
        // 1. Wake up the MPU6050
        i2C.writeRegister(MPU6050_REG_ADDR_PWR_MGMT_1, 0x00)
        println(" MPU6050 is awake.")

        mpu6050 = i2C

    }

    override fun reset() {

    }

    override fun getPositionData(): Position? {

        // 2. Read raw data
        val accelX = mpu6050.readRegisterWord(MPU6050_REG_ADDR_ACCEL_XOUT_H)
        val accelY = mpu6050.readRegisterWord(MPU6050_REG_ADDR_ACCEL_XOUT_H + 2)
        val accelZ = mpu6050.readRegisterWord(MPU6050_REG_ADDR_ACCEL_XOUT_H + 4)

        val gyroX = mpu6050.readRegisterWord(MPU6050_REG_ADDR_GYRO_XOUT_H)
        val gyroY = mpu6050.readRegisterWord(MPU6050_REG_ADDR_GYRO_XOUT_H + 2)
        val gyroZ = mpu6050.readRegisterWord(MPU6050_REG_ADDR_GYRO_XOUT_H + 4)

        // 3. Calculate real-world values
        val accelX_g = accelX / ACCEL_SCALE_FACTOR
        val accelY_g = accelY / ACCEL_SCALE_FACTOR
        val accelZ_g = accelZ / ACCEL_SCALE_FACTOR

        val gyroX_dps = gyroX / GYRO_SCALE_FACTOR
        val gyroY_dps = gyroY / GYRO_SCALE_FACTOR
        val gyroZ_dps = gyroZ / GYRO_SCALE_FACTOR

        // 4. Calculate Pitch and Roll from Accelerometer data
        val pitch = Math.toDegrees(atan2(-accelX_g, sqrt(accelY_g.pow(2) + accelZ_g.pow(2))))
        val roll = Math.toDegrees(atan2(accelY_g, accelZ_g))

        // Print the results using Kotlin's string templates
        val output = """
                    --> Accel (g): X=%.2f, Y=%.2f, Z=%.2f
                    --> Gyro (°/s): X=%.2f, Y=%.2f, Z=%.2f
                    --> Orientation: Pitch=%.2f°, Roll=%.2f°
                """.trimIndent().format(accelX_g, accelY_g, accelZ_g, gyroX_dps, gyroY_dps, gyroZ_dps, pitch, roll)

        println(output)




        return Position(0, "MPU6050", "BBOON")
    }

    override fun getGyroscopePositionData(): Position? {
        return Position(0, "MPU6050", "BBOON")
    }

    override fun getGPSPositionData(): Position? {
        return Position(0, "MPU6050", "BBOON")
    }


    companion object {
        const val MPU6050_REG_ADDR_PWR_MGMT_1: Int = 0x6B
        const val MPU6050_REG_ADDR_ACCEL_XOUT_H: Int = 0x3B
        const val MPU6050_REG_ADDR_GYRO_XOUT_H = 0x43
        const val ACCEL_SCALE_FACTOR: Double = 16384.0
        const val GYRO_SCALE_FACTOR: Double = 131.0
    }


}