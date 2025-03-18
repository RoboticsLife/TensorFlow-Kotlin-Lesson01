package brain.emitters

import brain.data.local.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object NetworkEmitters {

    private val _weatherEmitter = MutableSharedFlow<WeatherData>()
    val weatherEmitter = _weatherEmitter.asSharedFlow()

    fun emitWeatherResponse(weatherData: WeatherData) {
        CoroutineScope(Dispatchers.IO).launch {
            _weatherEmitter.emit(weatherData)
        }
    }

}