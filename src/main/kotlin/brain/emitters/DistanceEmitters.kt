package brain.emitters

import brain.data.Distance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object DistanceEmitters {

    private val _distanceSensor = MutableSharedFlow<Distance>()
    val distanceSensor = _distanceSensor.asSharedFlow()

    fun emitDistanceData(distance: Distance) {
        CoroutineScope(Dispatchers.IO).launch {
            _distanceSensor.emit(distance)
        }
    }
}