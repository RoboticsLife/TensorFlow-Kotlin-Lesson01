### Kotlin Gpio project. Working with IO lines on Raspberry Pi using Pi4J Kotlin/Java langs and remote compiling / debugging to any ARM GPIO compatible hardware. Advanced AI features (TensorFlow)


[All tutorails and videos on my YouTube channel](https://www.youtube.com/@OleksandrNeiko)



## LESSON 07: Buzzer sound


#### Step 1: Weather service API

https://openweathermap.org/current#name



#### Step 2: Retrofit Dependencies

````
            <!-- Retrofit connection -->
        <!-- https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit -->
        <dependency>
            <groupId>com.squareup.retrofit2</groupId>
            <artifactId>retrofit</artifactId>
            <version>2.11.0</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-jackson -->
        <dependency>
            <groupId>com.squareup.retrofit2</groupId>
            <artifactId>converter-jackson</artifactId>
            <version>2.11.0</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp -->
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>4.12.0</version>
        </dependency>
````

#### Step 3: Retrofit instance 

````
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
````

````
package network.weatherservice

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
            NetworkEmitters.WeatherData(
                weatherResponse = if (response.isSuccessful) response.body() else null,
                isSuccessful = response.isSuccessful,
                httpCode = response.code(),
                message = response.message()
            )
        )
     }
}
````


#### Step 4: Weather API calls interface

````
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
````

#### Step 5: Weather Response Data class

````
package network.weatherservice.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WeatherResponse(
    val base: String? = null,
    val clouds: Clouds? = null,
    val cod: Int? = null,
    val coord: Coord? = null,
    val dt: Int? = null,
    val id: Int? = null,
    val main: Main? = null,
    val name: String? = null,
    val rain: Rain? = null,
    val sys: Sys? = null,
    val timezone: Int? = null,
    val visibility: Int? = null,
    val weather: List<Weather?>? = null,
    val wind: Wind? = null
) {
    data class Clouds(
        val all: Int? = null
    )

    data class Coord(
        val lat: Double? = null,
        val lon: Double? = null
    )

    data class Main(
        val feels_like: Double? = null,
        val grnd_level: Int? = null,
        val humidity: Int? = null,
        val pressure: Int? = null,
        val sea_level: Int? = null,
        val temp: Double? = null,
        val temp_max: Double? = null,
        val temp_min: Double? = null
    )

    data class Rain(
        val `1h`: Double? = null
    )

    data class Sys(
        val country: String? = null,
        val id: Int? = null,
        val sunrise: Int? = null,
        val sunset: Int? = null,
        val type: Int? = null
    )

    data class Weather(
        val description: String? = null,
        val icon: String? = null,
        val id: Int? = null,
        val main: String? = null
    )

    data class Wind(
        val deg: Int? = null,
        val gust: Double? = null,
        val speed: Double? = null
    )
}
````

#### Step 6: Emitter as Kotlin SharedFlow to notify subscribers about weather

````
package brain.emitters

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import network.weatherservice.data.WeatherResponse

object NetworkEmitters {

    data class WeatherData(
        val weatherResponse: WeatherResponse? = null,
        val isSuccessful: Boolean = false,
        val httpCode: Int = 0,
        val message: String = ""
    )

    private val _weatherEmitter = MutableSharedFlow<WeatherData>()
    val weatherEmitter = _weatherEmitter.asSharedFlow()

    fun emitWeatherResponse(weatherData: WeatherData) {
        CoroutineScope(Dispatchers.IO).launch {
            _weatherEmitter.emit(weatherData)
        }
    }

}
````

#### Step 7: Main thread logic. Network call after button pressing and weather output

````
    val weatherNetworkService = WeatherNetworkService()

    if (avatar.type == HardwareTypes.Type.CIRCUIT_BOARD) {

        (avatar.body as CircuitBoard).addButtonListeners(
            buttonPosition = 0,
            actionHigh = {
                weatherNetworkService.getWeatherByName("toronto")
            },
            actionLow = {}
        )
    }

    NetworkEmitters.weatherEmitter.collect { weather ->
        if (weather.isSuccessful && weather.weatherResponse != null) {
            println(weather)
            val temp = weather.weatherResponse.main?.temp?.toInt()

            CoroutineScope(Dispatchers.IO).launch {
                //if temp = 0 -> blink 2 leds once
                if (temp != null && temp == 0) {
                    (avatar.body as CircuitBoard).ledOn(0, 1000L)
                    (avatar.body as CircuitBoard).ledOn(1, 1000L)
                } else if (temp != null && temp > 0) {
                    //if temp > 0 -> blink green led $temp times
                    for (i in 1..temp) {
                        (avatar.body as CircuitBoard).ledOn(0, 1000L)
                        delay(2000)
                    }
                } else if (temp != null && temp < 0) {
                    //if temp > 0 -> blink green led $temp times
                    for (i in 1..abs(temp)) {
                        (avatar.body as CircuitBoard).ledOn(1, 1000L)
                        delay(2000)
                    }
                }
            }
        }

    }
````

#### * Additional settings: remote compiling / debugging setup


Add new launch configuration to IntelliJ IDEA

![screenshot](readme/readme01.png)


fill IP / port adress to Raspberry PI. Username & password as sudo connection. Add Main Kotlin class and project module.

![screenshot](readme/readme02.png)
