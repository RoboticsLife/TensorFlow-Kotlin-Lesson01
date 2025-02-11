package avatar.hardware

object HardwareTypes {

    enum class Type {
        UNKNOWN,
        CIRCUIT_BOARD,
        WHEELS_ROBOT
    }

    class CircuitBoard {
        companion object {
            const val JSON_TYPE_NAME = "circuitboard"
        }
    }

    class WheelsRobot {
        companion object {
            const val JSON_TYPE_NAME = "wheelsrobot"
        }
    }
}