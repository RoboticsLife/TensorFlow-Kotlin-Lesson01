package network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object InternetConnection {

    private val okHttpClient: OkHttpClient? = null

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            //add interceptors if need
            .build()
    }

    fun getWeatherClient(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient ?: getOkHttpClient())
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }
 }