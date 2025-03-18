package brain.data.local

data class Distance(
    val sensorPosition: Int,
    val echoLowNanoTime: Long,
    val echoHighNanoTime: Long
)
