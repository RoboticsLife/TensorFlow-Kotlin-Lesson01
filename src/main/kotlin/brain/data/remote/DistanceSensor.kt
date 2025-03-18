package brain.data.remote

data class DistanceSensor(
    val config_id: String,
    val sensor_id: String,
    val time: Long,
    val unit: String,
    val value: Float,
    )
