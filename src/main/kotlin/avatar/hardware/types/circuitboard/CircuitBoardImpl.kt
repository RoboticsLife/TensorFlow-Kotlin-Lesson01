package avatar.hardware.types.circuitboard

import avatar.hardware.parts.*
import avatar.hardware.parts.basecomponents.Display
import avatar.hardware.parts.basecomponents.DistanceSensor
import avatar.hardware.parts.basecomponents.PositionSensor
import avatar.hardware.parts.basecomponents.Servo
import avatar.hardware.types.circuitboard.data.BodyCircuitBoard
import com.pi4j.context.Context
import kotlinx.coroutines.*
import brain.data.local.Configuration
import brain.data.local.Distance
import brain.data.local.Position
import brain.emitters.DistanceEmitters
import runtime.setup.Settings.CONNECTION_TYPE_I2C
import java.util.concurrent.TimeUnit

class CircuitBoardImpl(private val pi4J: Context, private val configuration: Configuration): CircuitBoard {

    override var body: BodyCircuitBoard = BodyCircuitBoard()
        private set

    init {
        initHardware()
    }

    private fun initHardware() {

        //PARSE CONFIG

        /** init Leds */
        configuration.leds?.forEach {
            if (it?.pin != null) {
                body.leds.add(Led(pi4J, it))
            }
        }

        /** init Buttons */
        configuration.buttons?.forEach {
            if (it?.pin != null) {
                body.buttons.add(Button(pi4J, it))
            }
        }

        /** init Buzzers */
        configuration.buzzers?.forEach {
            if (it?.pin != null) {
                body.buzzers.add(Buzzer(pi4J, it))
            }
        }

        /** init Sensors */
        configuration.distanceSensors?.forEach {
            if (it?.pinTrigger != null && it.pinEcho != null) {
                when (DistanceSensor.isConfigurationValid(it)) {
                    DistanceSensor.NAME_HARDWARE_MODEL_HC_SR_04 ->
                        body.distanceSensors.add(DistanceSensorHcSr04v2021(pi4J, it))
                }
            }
        }

        /** init displays */
        configuration.displays?.forEach {
            if (it?.hardwareModel != null) {
                when (Display.isConfigurationValid(it)) {
                    Display.NAME_HARDWARE_MODEL_3461BS_1 ->
                        body.displays.add(Display3461BS1(pi4J, it))
                    Display.NAME_HARDWARE_MODEL_LCD_1602 ->
                        if (it.connectionType?.lowercase() == CONNECTION_TYPE_I2C)
                        body.displays.add(DisplayLCD1602(pi4J, it))
                }
            }
        }

        /** init Servos */
        configuration.servos?.forEach {
            if (it?.pin != null) {
                when (Servo.isConfigurationValid(it)) {
                    Servo.NAME_HARDWARE_MODEL_SG90 ->
                        body.servos.add(ServoSG90(pi4J, it))
                }
            }
        }

        /** init Position Sensors */
        configuration.positionSensors?.forEach {
            if (it?.hardwareModel != null) {
                when (PositionSensor.isConfigurationValid(it)) {
                    PositionSensor.NAME_HARDWARE_MODEL_MPU6050 ->
                        body.positionSensors.add(MPU6050(pi4J, it))
                }
            }
        }

    }


    override fun getLedsCount(): Int = body.leds.size

    override fun ledOn(ledPosition: Int, durationInMillis: Long): Boolean {
        if (ledPosition < 0 || ledPosition >= body.leds.size) return false
        /** multithreading job */
        body.leds[ledPosition].threadScope?.cancel()
        body.leds[ledPosition].threadScope = CoroutineScope(Job() + Dispatchers.IO).launch {
            if (ledPosition < body.leds.size) {
                body.leds[ledPosition].on()

                if (durationInMillis > 0L) {
                    delay(durationInMillis)
                    body.leds[ledPosition].off()
                }
            }
            body.leds[ledPosition].threadScope?.cancel()
        }
        return true
    }

    override fun ledOff(ledPosition: Int): Boolean {
        if (ledPosition < 0 || ledPosition >= body.leds.size) return false
        if (ledPosition < body.leds.size) {
            body.leds[ledPosition].off()
            return true
        }
        return false
    }

    override fun addButtonListeners(buttonPosition: Int, actionHigh: () -> Unit, actionLow: () -> Unit): Boolean {
        if (buttonPosition < 0) return false

        if (buttonPosition < body.buttons.size) {
            body.buttons[buttonPosition].addButtonListeners(actionHigh, actionLow)
            return true
        }
        return false
    }

    override fun buzzerSoundOn(buzzerPosition: Int, durationInMillis: Long): Boolean {
        if (buzzerPosition < 0 || buzzerPosition >= body.buzzers.size) return false
        body.buzzers[buzzerPosition].threadScope?.cancel()
        body.buzzers[buzzerPosition].threadScope = CoroutineScope(Job() + Dispatchers.IO).launch {
            if (buzzerPosition < body.buzzers.size) {
                body.buzzers[buzzerPosition].soundOn()

                if (durationInMillis != 0L) {
                    delay(durationInMillis)
                    body.buzzers[buzzerPosition].soundOff()
                }
            }
            body.buzzers[buzzerPosition].threadScope?.cancel()
        }
        return true
    }

    override fun buzzerSoundOff(buzzerPosition: Int): Boolean {
        if (buzzerPosition < 0) return false

        if (buzzerPosition < body.buzzers.size) {
            body.buzzers[buzzerPosition].threadScope?.cancel()
            body.buzzers[buzzerPosition].soundOff()
            return true
        }
        return false
    }

    override fun startDistanceMeasuring(sensorPosition: Int, periodInMillis: Long): Boolean {
        if (sensorPosition >= body.distanceSensors.size) return false
        body.distanceSensors[sensorPosition].isActive = true

        body.distanceSensors[sensorPosition].threadScopeSensorRequest?.cancel()
        body.distanceSensors[sensorPosition].threadScopeSensorRequest = CoroutineScope(Job() + Dispatchers.IO).launch {
            /** Loop cycle while sensor is active */
            while (body.distanceSensors[sensorPosition].isActive) {
                body.distanceSensors[sensorPosition].triggerOutputHigh()
                TimeUnit.MICROSECONDS.sleep(10)
                body.distanceSensors[sensorPosition].triggerOutputLow()

                while (body.distanceSensors[sensorPosition].echoInput.isLow) {}
                val echoLowNanoTime = System.nanoTime()
                while (body.distanceSensors[sensorPosition].echoInput.isHigh) {}
                val echoHighNanoTime = System.nanoTime()

                DistanceEmitters.emitDistanceData(
                    Distance(
                        sensorPosition = sensorPosition,
                        echoHighNanoTime = echoHighNanoTime,
                        echoLowNanoTime = echoLowNanoTime
                    )
                )

                delay(periodInMillis)
            }
        }
        return true
    }

    override fun stopDistanceMeasuring(sensorPosition: Int): Boolean {
        body.distanceSensors[sensorPosition].isActive = false
        return true
    }

    override fun getDistanceMeasuringState(sensorPosition: Int): Boolean {
        return if (sensorPosition < body.distanceSensors.size) {
            body.distanceSensors[sensorPosition].isActive
        } else {
            false
        }
    }

    override fun displayPrint(displayPosition: Int, outFloat: Float?, string: String?, printTimeInMillis: Int?): Boolean {
        if (displayPosition < 0) return false

        if (displayPosition < body.displays.size) {
            return body.displays[displayPosition].outputPrint(outFloat, string, printTimeInMillis)
        }
        return false
    }

    override fun actuatorServoGetCurrentAngle(servoPosition: Int): Float {
        if (servoPosition < 0) return Float.POSITIVE_INFINITY

        if (servoPosition < body.servos.size) {
            return  body.servos[servoPosition].actuatorServoGetCurrentAngle()
        }
        return Float.POSITIVE_INFINITY
    }

    override fun actuatorServoGetAngleRangeLimit(servoPosition: Int): Float {
        if (servoPosition < 0) return Float.POSITIVE_INFINITY

        if (servoPosition < body.servos.size) {
            return  body.servos[servoPosition].actuatorServoGetAngleRangeLimit()
        }
        return Float.POSITIVE_INFINITY
    }

    override fun actuatorServoMoveToAngle(servoPosition: Int, angle: Float, customMovingTimeInMillis: Int?): Boolean {
        if (servoPosition < 0) return false

        if (servoPosition < body.servos.size) {
            return  body.servos[servoPosition].actuatorServoMoveToAngle(angle, customMovingTimeInMillis)
        }
        return false
    }

    override fun startPositionMeasuring(sensorPosition: Int, periodInMillis: Long): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun stopPositionMeasuring(sensorPosition: Int): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun getPositionData(sensorPosition: Int): Position? {
        if (sensorPosition < 0) return null

        if (sensorPosition < body.positionSensors.size) {
            return body.positionSensors[sensorPosition].getPositionData()
        }
        return null
    }

    override fun getGyroscopePositionData(sensorPosition: Int): Position? {
        if (sensorPosition < 0) return null

        if (sensorPosition < body.positionSensors.size) {
            return body.positionSensors[sensorPosition].getGyroscopePositionData()
        }
        return null
    }

    override fun getGPSPositionData(sensorPosition: Int): Position? {
        if (sensorPosition < 0) return null

        if (sensorPosition < body.positionSensors.size) {
            return body.positionSensors[sensorPosition].getGPSPositionData()
        }
        return null
    }

    override fun getBatteryStatus(): Int {
        return 0
    }

}