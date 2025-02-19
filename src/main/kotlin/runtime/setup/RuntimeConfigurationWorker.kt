package runtime.setup

import brain.data.Configuration

interface RuntimeConfigurationWorker {

    fun getConfiguration(fileName: String): Configuration
}