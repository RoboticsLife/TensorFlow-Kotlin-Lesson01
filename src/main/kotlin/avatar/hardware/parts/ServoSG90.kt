package avatar.hardware.parts

import brain.data.Configuration
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalOutputProvider
import com.pi4j.io.pwm.Pwm
import com.pi4j.io.pwm.PwmType

class ServoSG90(pi4j: Context, servoConfig: Configuration.ServoConfig): Servo {

    lateinit var pwm: Pwm
    private lateinit var ledOutput: DigitalOutput

    init {
        buildServo(pi4j, servoConfig)
    }

    private fun buildServo(pi4j: Context, servoConfig: Configuration.ServoConfig) {
        val params = Pwm.newConfigBuilder(pi4j)
            .id("BCM${servoConfig.pin}")
            .name(servoConfig.name)
            .address(servoConfig.pin)
            .pwmType(PwmType.HARDWARE)
        //    .provider("linuxfs-pwm")
            .initial(0)
            .frequency(50)
            .shutdown(0)
            .build()

        ledOutput = pi4j.digitalOutput<DigitalOutputProvider>().create(18)
     //   pwm = pi4j.create(params)

    }

    override fun rotateToAngle(angle: Int, speed: Float): Boolean {
        //TODO("Not yet implemented")
        println("ssdasdsad")
        //pwm.on(800)
        ledOutput.high()
        return true
    }
}