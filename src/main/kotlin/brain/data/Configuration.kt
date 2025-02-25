package brain.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Configuration(
    val configName: String? = null,
    val configDescription: String? = null,
    val configVersion: String? = null,
    val hardwareModel: String? = null,
    val hardwareModelCode: Int? = null,
    val hardwareType: String? = null,
    val hardwareTypeCode: Int? = null,
    val leds: List<LedConfig?>? = null,
    val buttons: List<ButtonConfig?>? = null,
    val buzzers: List<BuzzerConfig?>? = null,
    val distanceSensors: List<DistanceSensorConfig?>? = null,
    val displays: List<DisplayConfig?>? = null,
    val servos: List<ServoConfig?>? = null,
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ButtonConfig(
        val name: String?,
        val pin: Int?,
        val pullResistance: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class LedConfig(
        val name: String?,
        val pin: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class BuzzerConfig(
        val name: String?,
        val pin: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class DistanceSensorConfig(
        val name: String?,
        val hardwareModel: String?,
        val hardwareVersion: String?,
        val pinTrigger: Int?,
        val pinEcho: Int?,
        val installedSensorPosition: Int?,
        val movingAngle: Int? = 0
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class DisplayConfig(
        val name: String?,
        val hardwareModel: String?,
        val hardwareVersion: String?,
        val pin01: Int?,
        val pin02: Int?,
        val pin03: Int?,
        val pin04: Int?,
        val pin05: Int?,
        val pin06: Int?,
        val pin07: Int?,
        val pin08: Int?,
        val pin09: Int?,
        val pin10: Int?,
        val pin11: Int?,
        val pin12: Int?,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ServoConfig(
        val name: String?,
        val hardwareModel: String?,
        val hardwareVersion: String?,
        val pin: Int?,
        val installedServoPosition: Int?
    )
}