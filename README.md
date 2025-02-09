### Kotlin Gpio project. Working with IO lines on Raspberry Pi using Pi4J Kotlin/Java langs and remote compiling / debugging to any ARM GPIO compatible hardware. Advanced AI features (TensorFlow)


[All tutorails and videos on my YouTube channel](https://www.youtube.com/@OleksandrNeiko)



## Lesson 03: Hardware configs parsing, Hot Plugging presets


#### Step 1: maven pom.xml settings

###### Add json parsing libraries to pom.xml / Dependencies section

 <!-- JSON parsing -->
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
            <version>2.18.1</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.module</groupId>
            <artifactId>jackson-module-kotlin</artifactId>
            <version>2.18.1</version>
        </dependency>


#### Step 2: add resources folder and create json configuration file

````
{
    "configName": "lesson03-two-leds",
    "configDescription": "This is a simple configuration with 2 leds",
    "configVersion": "1.0.0",
    "hardwareType": "circuit",
    "hardwareModel": "leds",
    "hardwareTypeCode": 1,
    "hardwareModelCode": 1,
    "leds": [
        {
            "name": "green-led",
            "pin": 2
        },
        {
            "name": "red-led",
            "pin": 3
        }
    ],
    "buttons": [
        {
            "name": "ControlButton",
            "pin": 21,
            "pullResistance": 1
        }
    ]
}
````

#### Step 3: Create json configuration parser

````
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Configuration(
    val configName: String? = null,
    val configDescription: String? = null,
    val configVersion: String? = null,
    val hardwareModel: String? = null,
    val hardwareModelCode: Int? = null,
    val hardwareType: String? = null,
    val hardwareTypeCode: Int? = null,
    val leds: List<Led?>? = null,
    val buttons: List<Button?>? = null,
) {
    data class Button(
        val name: String?,
        val pin: Int?,
        val pullResistance: Int?
    )

    data class Led(
        val name: String?,
        val pin: Int?
    )
}
````


#### Step 4: Create kotlin data class for local configuration

````
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
````

#### Step 5: load runtime Configuration to Main clss
````
lateinit var configuration: Configuration

configuration = Injector.getRuntimeConfiguration().getConfiguration("lesson03config.json")
````


#### * Additional settings: remote compiling / debugging setup


Add new launch configuration to IntelliJ IDEA

![screenshot](readme/readme01.png)


fill IP / port adress to Raspberry PI. Username & password as sudo connection. Add Main Kotlin class and project module.

![screenshot](readme/readme02.png)
