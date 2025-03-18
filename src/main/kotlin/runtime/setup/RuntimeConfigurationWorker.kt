package runtime.setup

import brain.data.local.Configuration

interface RuntimeConfigurationWorker {

    fun getConfiguration(fileName: String): Configuration
}