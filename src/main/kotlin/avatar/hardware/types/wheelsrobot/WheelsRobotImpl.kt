package avatar.hardware.types.wheelsrobot

import avatar.hardware.types.wheelsrobot.data.BodyWheelsRobot
import com.pi4j.context.Context
import runtime.setup.Configuration

class WheelsRobotImpl(private val pi4J: Context, private val configuration: Configuration): WheelsRobot {
    override val body: BodyWheelsRobot = BodyWheelsRobot()

    override fun getBatteryStatus(): Int {
        return 1
    }

    //TODO

}