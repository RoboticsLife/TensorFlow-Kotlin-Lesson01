package network.weatherservice

import brain.data.local.WeatherData
import brain.emitters.NetworkEmitters
import network.InternetConnection
import runtime.setup.Settings

class WeatherNetworkService {

    companion object {
        const val WEATHER_BASE_URL = "https://api.openweathermap.org/"
    }

    private val client = InternetConnection.getWeatherClient(WEATHER_BASE_URL)
    private val apiService = client.create(Api::class.java)

    fun getWeatherByName(city: String, units: String = "metric") {
        val response = apiService.getWeatherByName(
            appid = Settings.WEATHER_API_KEY,
            city = city,
            units = units
        ).execute()

        NetworkEmitters.emitWeatherResponse(
            WeatherData(
                weatherResponse = if (response.isSuccessful) response.body() else null,
                isSuccessful = response.isSuccessful,
                httpCode = response.code(),
                message = response.message()
            )
        )
     }
}