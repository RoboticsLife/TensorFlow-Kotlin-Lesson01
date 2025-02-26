package avatar.hardware.parts

import brain.data.Configuration
import com.pi4j.context.Context
import com.pi4j.io.pwm.Pwm
import com.pi4j.io.pwm.PwmType
import kotlinx.coroutines.*

class ServoSG90(pi4j: Context, servoConfig: Configuration.ServoConfig): Servo {

    lateinit var pwm: Pwm
    private var threadScope: Job? = null
    private var customTimeMoveThreadScope: Job? = null
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

    private fun moveToAngleForCustomTime(angle: Float, customMovingTimeInMillis: Int) {
        if (customMovingTimeInMillis == 0) return
        val filteredAngle =
            if (angle > DEFAULT_MAX_ANGLE) DEFAULT_MAX_ANGLE else if (angle < DEFAULT_MIN_ANGLE) DEFAULT_MIN_ANGLE else angle

        val angleMovementRange = currentPositionInDegrees - filteredAngle
        val range = if (filteredAngle % 1 > 0) angleMovementRange.toInt() + 1 else angleMovementRange.toInt()

        customTimeMoveThreadScope?.cancel()
        customTimeMoveThreadScope = CoroutineScope(Job() + Dispatchers.IO).launch {
            for (i in 1..range) {
                delay((customMovingTimeInMillis / range).toLong())
                val angleStep = if (i < range) i.toFloat() else angleMovementRange - range.toFloat() - (if (filteredAngle % 1 > 0) 1.0f else 0.0f)
                actuatorServoMoveToAngle(
                    angle = if (currentPositionInDegrees < filteredAngle) currentPositionInDegrees + angleStep else currentPositionInDegrees - angleStep
                )
            }
        }
    }


    override fun actuatorServoGetCurrentAngle(): Float {
        return currentPositionInDegrees
    }

    override fun actuatorServoGetAngleRangeLimit(): Float {
        return DEFAULT_ANGLE_RANGE
    }

    override fun actuatorServoMoveToAngle(angle: Float, customMovingTimeInMillis: Int?): Boolean {
        if (customMovingTimeInMillis != null &&customMovingTimeInMillis > 0) {
            moveToAngleForCustomTime(angle, customMovingTimeInMillis)
        } else {
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
        return false
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