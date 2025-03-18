package network.databases


interface DatabaseInitializer {

    fun initializeDatabase()

    fun read(tableName: String, data: Any?)

    fun write(tableName: String, data: Any)

    companion object {
        const val DB_TABLE_NAME_DISTANCE_SENSORS = "distance-sensors"

    }

}