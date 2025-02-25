package avatar.hardware.types.circuitboard

import avatar.hardware.Body

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

    fun rotateToAngle(servoPosition: Int = 0, angle: Int, speed: Float = 1f): Boolean

}