package runtime.setup

interface RuntimeConfigurationWorker {

    fun getConfiguration(fileName: String): Configuration
}