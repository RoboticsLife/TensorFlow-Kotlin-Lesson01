package avatar.hardware.parts.java

import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import java.time.Duration

abstract class I2CDevice protected constructor(pi4j: Context, device: Int, name: String) :
    Component() {
    /**
     * The PI4J I2C component
     */
    private val i2c: I2C


    init {
        i2c = pi4j.create(
            I2C.newConfigBuilder(pi4j)
                .id("I2C-" + DEFAULT_BUS + "@" + device)
                .name("$name@$device")
                .bus(DEFAULT_BUS)
                .device(device)
                .build()
        )
        initState(i2c)
        logDebug("I2C device %s initialized", name)
    }


    /**
     * send a single command to device
     */
    protected fun sendCommand(cmd: Byte) {
        i2c.write(cmd)
        delay(Duration.ofNanos(100000))
    }

    protected fun readRegister(register: Int): Int {
        return i2c.readRegisterWord(register)
    }

    /**
     * send custom configuration to device
     *
     * @param config custom configuration
     */
    protected fun writeRegister(register: Int, config: Int) {
        i2c.writeRegisterWord(register, config)
    }

    /**
     * send some data to device
     *
     * @param data
     */
    protected fun write(data: Byte) {
        i2c.write(data)
    }

    /**
     * Execute Display commands
     *
     * @param command Select the LCD Command
     * @param data    Setup command data
     */
    protected fun sendCommand(command: Byte, data: Byte) {
        sendCommand((command.toInt() or data.toInt()).toByte())
    }

    protected abstract fun initState(i2c: I2C?) // --------------- for testing --------------------


    companion object {
        /**
         * The Default BUS and Device Address.
         * On the PI, you can look it up with the Command 'sudo i2cdetect -y 1'
         */
        protected const val DEFAULT_BUS: Int = 0x01
    }
}
