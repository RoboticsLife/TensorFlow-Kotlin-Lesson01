package runtime.setup

import brain.data.Configuration
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class RuntimeConfigurationWorkerImpl: RuntimeConfigurationWorker {

    override fun getConfiguration(fileName: String): Configuration {
        val mapper = jacksonObjectMapper()
        mapper.registerKotlinModule()
        mapper.registerModule(JavaTimeModule())
        try {
            val jsonString = javaClass.classLoader.getResource(fileName)?.readText()
            val jsonTextConfig: Configuration = mapper.readValue(jsonString, Configuration::class.java)
            return jsonTextConfig
        } catch (e: Exception) {
            return Configuration()
        }
    }
}