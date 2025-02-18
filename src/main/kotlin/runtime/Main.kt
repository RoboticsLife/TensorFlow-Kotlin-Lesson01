package org.example.runtime

import avatar.Avatar
import avatar.hardware.AvatarBuilder
import avatar.hardware.HardwareTypes
import avatar.hardware.types.circuitboard.CircuitBoard
import brain.emitters.NetworkEmitters
import com.pi4j.context.Context
import com.pi4j.util.Console
import kotlinx.coroutines.*
import network.weatherservice.WeatherNetworkService
import runtime.setup.Configuration
import runtime.setup.Injector
import kotlin.math.abs


/**
 * LESSON 07: Buzzer sound
 * GPIO-Kotlin-Pi4j project.
 * Kotlin Gpio project. Working with IO lines on Raspberry Pi using Pi4J Kotlin/Java langs and remote
 * compiling / debugging to any ARM GPIO compatible hardware. Advanced AI features (TensorFlow)
 */

//Hardware
lateinit var pi4j: Context
lateinit var console: Console
lateinit var configuration: Configuration
lateinit var avatar: Avatar

suspend fun main() {

    init()

    //Print out
    console.println(pi4j.boardInfo().boardModel)
    println(configuration)

    val weatherNetworkService = WeatherNetworkService()

    if (avatar.type == HardwareTypes.Type.CIRCUIT_BOARD) {

        (avatar.body as CircuitBoard).addButtonListeners(
            buttonPosition = 0,
            actionHigh = {
                weatherNetworkService.getWeatherByName("toronto")
                (avatar.body as CircuitBoard).buzzerSoundOn(0)
            },
            actionLow = {
                (avatar.body as CircuitBoard).buzzerSoundOff(0)
            }
        )
    }

    NetworkEmitters.weatherEmitter.collect { weather ->
        if (weather.isSuccessful && weather.weatherResponse != null) {
            println(weather)
            val temp = weather.weatherResponse.main?.temp?.toInt()

            CoroutineScope(Dispatchers.IO).launch {
                //if temp = 0 -> blink 2 leds once
                if (temp != null && temp == 0) {
                    (avatar.body as CircuitBoard).ledOn(0, 1000L)
                    (avatar.body as CircuitBoard).ledOn(1, 1000L)
                } else if (temp != null && temp > 0) {
                    //if temp > 0 -> blink green led $temp times
                    for (i in 1..temp) {
                        (avatar.body as CircuitBoard).ledOn(0, 1000L)
                        delay(2000)
                    }
                } else if (temp != null && temp < 0) {
                    //if temp > 0 -> blink green led $temp times
                    for (i in 1..abs(temp)) {
                        (avatar.body as CircuitBoard).ledOn(1, 1000L)
                        delay(2000)
                    }
                }

                (avatar.body as CircuitBoard).buzzerSoundOn(0, 500)
                delay(1000)
                (avatar.body as CircuitBoard).buzzerSoundOn(0, 500)
                delay(1000)
                (avatar.body as CircuitBoard).buzzerSoundOn(0, 500)

            }
        }

    }

}


fun init() {
    pi4j = Injector.getPi4j()
    console = Injector.getPi4jConsole()
    configuration = Injector.getRuntimeConfiguration().getConfiguration("lesson03config.json")
    avatar = AvatarBuilder(pi4j, configuration).build()
}
