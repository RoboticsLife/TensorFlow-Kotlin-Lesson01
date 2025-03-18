package brain

import avatar.Avatar
import brain.data.remote.DistanceSensor
import brain.emitters.DistanceEmitters
import brain.utils.toCm
import kotlinx.coroutines.*
import network.databases.DatabaseConnector
import network.databases.DatabaseInitializer

class Brain {

    private lateinit var avatar: Avatar
    private lateinit var dataBaseFirebaseFirestore: DatabaseConnector

    //Threads
    private var devicesThreadScopeArray: MutableMap<String, Job?> = mutableMapOf()

    private fun init() {
        initDatabases()
    }

    fun build(avatar: Avatar): Brain {
        this.avatar = avatar
        init()
        return this
    }

    private fun initDatabases() {
        dataBaseFirebaseFirestore = DatabaseConnector(type = DatabaseConnector.DatabaseTypes.FIREBASE_FIRESTORE_DB)
    }

    fun readFromMemory(parameterName: String, key: Any?) {
        dataBaseFirebaseFirestore.readFromDB(parameterName, key)
    }

    fun rememberToMemory(parameterName: String, data: Any) {
        dataBaseFirebaseFirestore.writeToDB(parameterName, data)
    }

    fun startTrackDevice(parameterName: String, devicePosition: Int? = null, loggingPeriodInMillis: Long = 1000) {
        when (parameterName) {
            PARAMETER_SENSOR_DISTANCE -> {
                subscribeToDistanceEmitters(devicePosition, loggingPeriodInMillis)
            }
        }
    }

    fun stopTrackDevice(parameterName: String, devicePosition: Int? = null) {
        devicesThreadScopeArray["$parameterName${devicePosition.toString()}"]?.cancel()
        devicesThreadScopeArray["$parameterName${devicePosition.toString()}"] = null
        devicesThreadScopeArray.remove("$parameterName${devicePosition.toString()}")
    }


    private fun subscribeToDistanceEmitters(sensorPosition: Int? = null, loggingPeriodInMillis: Long = 1000) {
        var launchTime = System.currentTimeMillis()
        val threadScope = CoroutineScope(Job() + Dispatchers.IO).launch {
            DistanceEmitters.distanceSensor.collect { distance ->
                if (System.currentTimeMillis() >= launchTime+loggingPeriodInMillis ) {
                    rememberToMemory(
                        parameterName = DatabaseInitializer.DB_TABLE_NAME_DISTANCE_SENSORS,
                        data = DistanceSensor(
                            config_id = avatar.configuration?.configName.toString(),
                            sensor_id = distance.sensorPosition.toString(),
                            time = distance.echoLowNanoTime,
                            unit = "cm",
                            value = distance.toCm()
                        )
                    )
                    launchTime = System.currentTimeMillis()
                }
            }

        }

        devicesThreadScopeArray["$PARAMETER_SENSOR_DISTANCE${sensorPosition.toString()}"] = threadScope
    }


    companion object {
        const val PARAMETER_SENSOR_DISTANCE = "sensorDistance"
        //add more...
    }

}