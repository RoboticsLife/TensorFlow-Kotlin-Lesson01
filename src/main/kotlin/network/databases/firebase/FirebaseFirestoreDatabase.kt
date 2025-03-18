package network.databases.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import network.databases.DatabaseInitializer
import java.io.FileInputStream

class FirebaseFirestoreDatabase: DatabaseInitializer {

    private var db: Firestore? = null

    init {
        initializeDatabase()
    }

    override fun initializeDatabase() {

        try {

            val res = javaClass.classLoader.getResource("firebase-service-account.json")

            val serviceAccount = FileInputStream(res.path)

            val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://robotics-public-default-rtdb.firebaseio.com")
                .build()

            FirebaseApp.initializeApp(options)
            db = FirestoreClient.getFirestore()


        } catch (e: Exception) {
            println(e.message)
        }
    }



    override fun read(tableName: String, data: Any?) {
        //TODO
    }

    override fun write(tableName: String, data: Any) {
        try {
            if (db != null) {
                val collection = db!!.collection(tableName).document()
                collection.set(data)
            }
        } catch (_: Exception) {}
    }
}