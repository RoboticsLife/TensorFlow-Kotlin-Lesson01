package runtime.setup

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
) {
    data class ButtonConfig(
        val name: String?,
        val pin: Int?,
        val pullResistance: Int?
    )

    data class LedConfig(
        val name: String?,
        val pin: Int?
    )

    data class BuzzerConfig(
        val name: String?,
        val pin: Int?
    )
}