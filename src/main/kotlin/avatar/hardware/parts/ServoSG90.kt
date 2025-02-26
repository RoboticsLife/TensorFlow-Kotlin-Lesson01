package avatar.hardware.parts

import brain.data.Configuration
import com.pi4j.context.Context
import com.pi4j.io.pwm.Pwm
import com.pi4j.io.pwm.PwmType
import kotlinx.coroutines.*

class ServoSG90(pi4j: Context, servoConfig: Configuration.ServoConfig): Servo {

    lateinit var pwm: Pwm
    private var threadScope: Job? = null
    private var currentPositionInDegrees: Float = 0f

    init {
        buildServo(pi4j, servoConfig)
        moveToDefaultAngle()
    }

    private fun buildServo(pi4j: Context, servoConfig: Configuration.ServoConfig) {
        val params = Pwm.newConfigBuilder(pi4j)
            .id("BCM${servoConfig.pin}")
            .name(servoConfig.name)
            .address(servoConfig.pin)
            .pwmType(PwmType.HARDWARE)
            .initial(0)
            .frequency(DEFAULT_FREQUENCY)
            .shutdown(0)
            .build()

        pwm = pi4j.create(params)
    }

    private fun moveToDefaultAngle() {
        actuatorServoMoveToAngle(angle = 0f)
    }


    override fun actuatorServoGetCurrentAngle(): Float {
        return currentPositionInDegrees
    }

    override fun actuatorServoGetAngleRangeLimit(): Float {
        return DEFAULT_ANGLE_RANGE
    }

    override fun actuatorServoMoveToAngle(angle: Float): Boolean {
        val filteredAngle =
            if (angle > DEFAULT_MAX_ANGLE) DEFAULT_MAX_ANGLE else if (angle < DEFAULT_MIN_ANGLE) DEFAULT_MIN_ANGLE else angle


        threadScope?.cancel()
        threadScope = CoroutineScope(Job() + Dispatchers.IO).launch {
            currentPositionInDegrees = filteredAngle
            val dutyCycleByOnDegree = (DEFAULT_MAX_DUTY_CYCLE - DEFAULT_MIN_DUTY_CYCLE) / DEFAULT_ANGLE_RANGE
            pwm.on(DEFAULT_START_POSITION_DUTY_CYCLE + filteredAngle * dutyCycleByOnDegree)
            delay(20)
        }
        return true
    }

    companion object {
        //from SG90 datasheet
        const val DEFAULT_FREQUENCY: Int = 50
        const val DEFAULT_MIN_ANGLE: Float = -90.0f
        const val DEFAULT_MAX_ANGLE: Float = 90.0f
        const val DEFAULT_ANGLE_RANGE: Float = 180f
        const val DEFAULT_MIN_DUTY_CYCLE: Float = 2.0f
        const val DEFAULT_MAX_DUTY_CYCLE: Float = 12.0f
        const val DEFAULT_START_POSITION_DUTY_CYCLE: Float =
            (DEFAULT_MAX_DUTY_CYCLE - DEFAULT_MIN_DUTY_CYCLE) / (DEFAULT_ANGLE_RANGE / DEFAULT_MAX_ANGLE) + DEFAULT_MIN_DUTY_CYCLE
        const val SPEED_PER_DEGREE_IN_MILLIS: Float = 100.0f / 60.0f
    }
}