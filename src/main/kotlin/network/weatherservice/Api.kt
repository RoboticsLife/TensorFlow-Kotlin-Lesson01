package network.weatherservice

import network.weatherservice.data.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("data/2.5/weather")
    fun getWeatherByName(
        @Query("appid") appid: String,
        @Query("q") city: String,
        @Query("units") units: String
    ): Call<WeatherResponse>
}