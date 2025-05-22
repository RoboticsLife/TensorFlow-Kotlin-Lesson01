package avatar.hardware.parts.basecomponents


import brain.data.local.I2CDeviceConfiguration
import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import java.io.IOException
import java.time.Duration

abstract class I2CDevice(pi4j: Context, i2CDeviceConfiguration: I2CDeviceConfiguration) {

    private var i2c: I2C = pi4j.create(
        I2C.newConfigBuilder(pi4j)
            .id("I2C- ${i2CDeviceConfiguration.hardwareModel} BUS- ${i2CDeviceConfiguration.address}")
            .name(i2CDeviceConfiguration.name)
            .bus(i2CDeviceConfiguration.pinSDA)
            .device(i2CDeviceConfiguration.address)
            .build()
    )

    init {
        this.initDevice(this.i2c)
    }

    abstract fun initDevice(i2C: I2C)

    abstract fun reset()

    protected fun sendCommand(cmd: Byte) {
        i2c.write(cmd)
        delay(Duration.ofNanos(100000))
    }

    protected fun readRegister(register: Int): Int {
        return i2c.readRegisterWord(register)
    }

    protected fun writeRegister(register: Int, config: Int) {
        i2c.writeRegisterWord(register, config)
    }

    protected fun write(data: Byte) {
        i2c.write(data)
    }

    /**
     * Reads the content of the reg register (8bits),
     * and returns this value in the 0..255 interval if read operation was successfull.
     * A negative number is returned for an error.
     * @param reg the local address on the i2c device where the data must be read.
     * @return the byte value read on the device: a positive number in the 0..255 interval if reading was successful;
     * a negative number (<0) if reading failed.
     * @see com.pi4j.io.i2c.I2CDevice.read
     * @see I2CComponent.readSignedRegisterValue
     */
    fun readUnsignedRegisterValue(reg: Int): Int {
        try {
            val result: Int = i2c.readRegisterWord(reg)
            if (result < 0) println("Error when reading i2c register content. Error=$result")
            return result
        } catch (e: IOException) {
            throw Exception(
                "The content of the register " + reg + " can not be read from the i2c device or i2c bus.",
                e
            )
        }
    }

    protected fun sendCommand(command: Byte, data: Byte) {
        sendCommand((command.toInt() or data.toInt()).toByte())
    }


    protected fun delay(duration: Duration){
        try {
            val nanos = duration.toNanos()
            val millis = nanos / 1000000
            val remainingNanos = (nanos % 1000000).toInt()
            Thread.sleep(millis, remainingNanos)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

}