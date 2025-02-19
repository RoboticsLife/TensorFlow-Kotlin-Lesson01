package brain.data

data class Distance(
    val sensorPosition: Int,
    val echoLowNanoTime: Long,
    val echoHighNanoTime: Long
)
