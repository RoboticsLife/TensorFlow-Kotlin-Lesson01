package avatar.hardware.types.circuitboard

import avatar.hardware.Body

interface CircuitBoard: Body {

    fun getLedsCount(): Int

    fun ledOn(ledPosition: Int = 0, durationInMillis: Long = 0L): Boolean

    fun ledOff(ledPosition: Int = 0): Boolean

    fun addButtonListeners(buttonPosition: Int = 0, actionHigh: () -> Unit, actionLow: () -> Unit): Boolean

    fun buzzerSoundOn(buzzerPosition: Int = 0, durationInMillis: Long = 0L): Boolean

    fun buzzerSoundOff(buzzerPosition: Int = 0): Boolean

}