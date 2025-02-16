package network

import network.data.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface Api {

    @GET("data/2.5/weather")
    fun getWeatherByName(
        @Url url: String,
        @Query("appid") appid: String,
        @Query("q") city: String,
        @Query("units") units: String
    ): Call<WeatherResponse>
}