package avatar.hardware.types.circuitboard.data

import avatar.body.BodyPrototype
import avatar.hardware.HardwareTypes
import avatar.hardware.parts.*
import avatar.hardware.parts.basecomponents.Display
import avatar.hardware.parts.basecomponents.DistanceSensor
import avatar.hardware.parts.basecomponents.PositionSensor
import avatar.hardware.parts.basecomponents.Servo

data class BodyCircuitBoard(
    override var type: HardwareTypes.Type = HardwareTypes.Type.CIRCUIT_BOARD,
    val leds: MutableList<Led> = mutableListOf(),
    val buttons: MutableList<Button> = mutableListOf(),
    val buzzers: MutableList<Buzzer> = mutableListOf(),
    val distanceSensors: MutableList<DistanceSensor> = mutableListOf(),
    val displays: MutableList<Display> = mutableListOf(),
    val servos: MutableList<Servo> = mutableListOf(),
    val positionSensors: MutableList<PositionSensor> = mutableListOf(),
    //TODO: Add hardware parts if need

): BodyPrototype()