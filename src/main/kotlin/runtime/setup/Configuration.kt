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
    val leds: List<Led?>? = null,
    val buttons: List<Button?>? = null,
) {
    data class Button(
        val name: String?,
        val pin: Int?,
        val pullResistance: Int?
    )

    data class Led(
        val name: String?,
        val pin: Int?
    )
}