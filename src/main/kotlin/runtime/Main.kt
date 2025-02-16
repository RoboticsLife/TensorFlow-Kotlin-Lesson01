package org.example.runtime

import avatar.Avatar
import avatar.hardware.AvatarBuilder
import avatar.hardware.HardwareTypes
import avatar.hardware.types.circuitboard.CircuitBoard
import com.pi4j.context.Context
import com.pi4j.util.Console
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import network.InternetConnection
import runtime.setup.Configuration
import runtime.setup.Injector


/**
 * LESSON 05: Work with button. Button listeners.
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

    if (avatar.type == HardwareTypes.Type.CIRCUIT_BOARD) {
        var counter = 0

        (avatar.body as CircuitBoard).addButtonListeners(
            buttonPosition = 0,
            actionHigh = {
                counter++
                (avatar.body as CircuitBoard).ledOn(0)
            },
            actionLow =  {
                (avatar.body as CircuitBoard).ledOff(0)
                if (counter > 4) {
                    (avatar.body as CircuitBoard).ledOn(1, 5000L)
                    counter = 0
                }
            }
        )

    }

    //add infinite loop for java app running
    coroutineScope {
        println("Start infinite main thread")
        delay(Long.MAX_VALUE)
        println("End infinite main thread")
    }
}


fun init() {
    pi4j = Injector.getPi4j()
    console = Injector.getPi4jConsole()
    configuration = Injector.getRuntimeConfiguration().getConfiguration("lesson03config.json")
    avatar = AvatarBuilder(pi4j, configuration).build()
}
