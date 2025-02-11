package avatar.hardware.types.circuitboard

import avatar.hardware.Body

interface CircuitBoard: Body {

    //TODO: add emitters

    fun getLedsCount(): Int

    fun ledOn(ledPosition: Int = 0, durationInMillis: Long = 0L): Boolean

    fun ledOff(ledPosition: Int = 0): Boolean

}