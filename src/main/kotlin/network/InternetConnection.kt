package network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object InternetConnection {

    private val okHttpClient = OkHttpClient()
        .newBuilder()
        //add interceptors if need
        .build()

    const val WEATHER_BASE_URL = "https://api.openweathermap.org/"

    fun getClient(): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()



    fun getApiService(): Api = getClient().create(Api::class.java)
}