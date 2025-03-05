package avatar.hardware.parts.basecomponents


import brain.data.I2CDeviceConfiguration
import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
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