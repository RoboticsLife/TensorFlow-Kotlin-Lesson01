package brain.data.local

import network.weatherservice.data.WeatherResponse

data class WeatherData(
    val weatherResponse: WeatherResponse? = null,
    val isSuccessful: Boolean = false,
    val httpCode: Int = 0,
    val message: String = ""
)
