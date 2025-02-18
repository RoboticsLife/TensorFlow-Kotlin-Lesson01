package avatar.hardware.types.circuitboard.data

import avatar.body.BodyPrototype
import avatar.hardware.HardwareTypes
import avatar.hardware.parts.Button
import avatar.hardware.parts.Buzzer
import avatar.hardware.parts.Led

data class BodyCircuitBoard(
    override var type: HardwareTypes.Type = HardwareTypes.Type.CIRCUIT_BOARD,
    val leds: MutableList<Led> = mutableListOf(),
    val buttons: MutableList<Button> = mutableListOf(),
    val buzzers: MutableList<Buzzer> = mutableListOf(),
    //TODO: Add hardware parts if need

): BodyPrototype()