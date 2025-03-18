package network.databases

import network.databases.firebase.FirebaseFirestoreDatabase

class DatabaseConnector(type: DatabaseTypes) {

    private lateinit var databaseConnector: DatabaseInitializer

    enum class DatabaseTypes() {
        FIREBASE_FIRESTORE_DB,
        LOCAL_DB,
    }

    init {
        when(type) {
            DatabaseTypes.FIREBASE_FIRESTORE_DB -> {
                databaseConnector = FirebaseFirestoreDatabase()
            }
            DatabaseTypes.LOCAL_DB -> {
                //TODO
            }
            else -> {
                databaseConnector = FirebaseFirestoreDatabase()
            }
        }
    }

    fun readFromDB(parameterName: String, key: Any?) {
        databaseConnector.read(parameterName, key)
    }

    fun writeToDB(tableName: String, data: Any) {
        databaseConnector.write(tableName, data)
    }

}