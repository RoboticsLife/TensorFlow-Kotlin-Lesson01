package avatar.hardware.types.circuitboard

import avatar.hardware.Body
import brain.data.local.Position

interface CircuitBoard: Body {

    fun getLedsCount(): Int

    fun ledOn(ledPosition: Int = 0, durationInMillis: Long = 0L): Boolean

    fun ledOff(ledPosition: Int = 0): Boolean

    fun addButtonListeners(buttonPosition: Int = 0, actionHigh: () -> Unit, actionLow: () -> Unit): Boolean

    fun buzzerSoundOn(buzzerPosition: Int = 0, durationInMillis: Long = 0L): Boolean

    fun buzzerSoundOff(buzzerPosition: Int = 0): Boolean

    fun startDistanceMeasuring(sensorPosition: Int = 0, periodInMillis: Long = 500): Boolean

    fun stopDistanceMeasuring(sensorPosition: Int = 0): Boolean

    fun getDistanceMeasuringState(sensorPosition: Int = 0): Boolean

    fun displayPrint(displayPosition: Int = 0, outFloat: Float? = null, string: String? = null, printTimeInMillis: Int? = 0): Boolean

    fun actuatorServoGetCurrentAngle(servoPosition: Int = 0): Float

    fun actuatorServoGetAngleRangeLimit(servoPosition: Int = 0): Float

    fun actuatorServoMoveToAngle(servoPosition: Int = 0, angle: Float = 0f, customMovingTimeInMillis: Int? = 0): Boolean

    fun startPositionMeasuring(sensorPosition: Int = 0, periodInMillis: Long = 100): Boolean

    fun stopPositionMeasuring(sensorPosition: Int = 0): Boolean

    fun getPositionData(sensorPosition: Int = 0): Position?

    fun getGyroscopePositionData(sensorPosition: Int = 0): Position?

    fun getGPSPositionData(sensorPosition: Int = 0): Position?

}