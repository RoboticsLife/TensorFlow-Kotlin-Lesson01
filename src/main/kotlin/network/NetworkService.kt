package network

import runtime.setup.Settings

class NetworkService {

    private val client = InternetConnection.getClient()
    private val apiService = client.create(Api::class.java)

    fun getWeatherByName(city: String, units: String = "metric") {
        val response = apiService.getWeatherByName(
            url = InternetConnection.WEATHER_BASE_URL,
            appid = Settings.WEATHER_API_KEY,
            city = city,
            units = units
        )
            .execute()
    }
}