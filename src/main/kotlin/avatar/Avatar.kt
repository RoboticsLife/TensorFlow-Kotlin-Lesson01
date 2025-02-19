package avatar

import avatar.hardware.Body
import avatar.hardware.HardwareTypes
import avatar.hardware.types.circuitboard.CircuitBoardImpl
import avatar.hardware.types.wheelsrobot.WheelsRobotImpl
import com.pi4j.context.Context
import brain.data.Configuration

/** main class to interact with hardware devices */
class Avatar {

    //res
    private lateinit var pi4J: Context
    private var configuration: Configuration? = null
    //hardware providers
    lateinit var body: Body
    lateinit var type: HardwareTypes.Type

    private fun init() {
        parseConfiguration()
    }


    fun build(pi4J: Context, configuration: Configuration): Avatar {
        this.pi4J = pi4J
        this.configuration = configuration
        init()
        return this
    }

    private fun parseConfiguration() {
        if (configuration == null) return

        when (configuration?.hardwareType) {

            HardwareTypes.CircuitBoard.JSON_TYPE_NAME -> {
                type = HardwareTypes.Type.CIRCUIT_BOARD
                body = CircuitBoardImpl(pi4J, configuration ?: Configuration())
            }

            HardwareTypes.WheelsRobot.JSON_TYPE_NAME -> {
                type = HardwareTypes.Type.WHEELS_ROBOT
                body = WheelsRobotImpl(pi4J, configuration ?: Configuration())
            }

        }

    }

}