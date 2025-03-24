package brain.emitters

import brain.data.local.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object PositionEmitters  {

    private val _positionSensor = MutableSharedFlow<Position>()
    val positionSensor = _positionSensor.asSharedFlow()

    fun emitPositionData(position: Position) {
        CoroutineScope(Dispatchers.IO).launch {
            _positionSensor.emit(position)
        }
    }
}