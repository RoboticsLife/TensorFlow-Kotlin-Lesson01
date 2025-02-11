package avatar.hardware

import avatar.body.BodyPrototype

interface Body {
    //shared logic to all body types

    val body: BodyPrototype

    fun getBatteryStatus(): Int
}