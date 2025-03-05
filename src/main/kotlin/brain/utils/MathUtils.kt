package brain.utils

import avatar.hardware.parts.basecomponents.DistanceSensor
import brain.data.Distance
import runtime.setup.Settings

fun Distance.toCm(sensorType: String = SENSOR_TYPE_UNKNOWN): Float {
    val cm = when(sensorType) {
        DistanceSensor.NAME_HARDWARE_MODEL_HC_SR_04 -> (echoHighNanoTime - echoLowNanoTime) / 1000F / Settings.DISTANCE_SENSOR_HC_SR04_DIVIDER_TO_CM
        SENSOR_TYPE_UNKNOWN -> (echoHighNanoTime - echoLowNanoTime) / 1000F / Settings.DISTANCE_SENSOR_HC_SR04_DIVIDER_TO_CM
        else -> (echoHighNanoTime - echoLowNanoTime) / 1000F / Settings.DISTANCE_SENSOR_HC_SR04_DIVIDER_TO_CM
    }
    return if (cm < Settings.DISTANCE_SENSOR_HC_SR04_MAX_LIMIT_CM) cm else Float.POSITIVE_INFINITY
}

fun Distance.toInch(sensorType: String = SENSOR_TYPE_UNKNOWN): Float {
    val cm = when(sensorType) {
        DistanceSensor.NAME_HARDWARE_MODEL_HC_SR_04 -> (echoHighNanoTime - echoLowNanoTime) / 1000F / Settings.DISTANCE_SENSOR_HC_SR04_DIVIDER_TO_INCH
        SENSOR_TYPE_UNKNOWN -> (echoHighNanoTime - echoLowNanoTime) / 1000F / Settings.DISTANCE_SENSOR_HC_SR04_DIVIDER_TO_INCH
        else -> (echoHighNanoTime - echoLowNanoTime) / 1000F / Settings.DISTANCE_SENSOR_HC_SR04_DIVIDER_TO_INCH
    }
    return if (cm < Settings.DISTANCE_SENSOR_HC_SR04_MAX_LIMIT_INCH) cm else Float.POSITIVE_INFINITY
}

const val SENSOR_TYPE_UNKNOWN = "Unknown"