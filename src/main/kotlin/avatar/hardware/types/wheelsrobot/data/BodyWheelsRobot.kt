package avatar.hardware.types.wheelsrobot.data

import avatar.body.BodyPrototype
import avatar.hardware.HardwareTypes

data class BodyWheelsRobot(
    override var type: HardwareTypes.Type = HardwareTypes.Type.WHEELS_ROBOT,
    //TODO add parts
): BodyPrototype()
