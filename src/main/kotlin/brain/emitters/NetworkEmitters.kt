package brain.emitters

import brain.data.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import network.weatherservice.data.WeatherResponse

object NetworkEmitters {

    private val _weatherEmitter = MutableSharedFlow<WeatherData>()
    val weatherEmitter = _weatherEmitter.asSharedFlow()

    fun emitWeatherResponse(weatherData: WeatherData) {
        CoroutineScope(Dispatchers.IO).launch {
            _weatherEmitter.emit(weatherData)
        }
    }

}