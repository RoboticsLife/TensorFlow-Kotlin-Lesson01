package org.example.runtime

import avatar.Avatar
import avatar.hardware.AvatarBuilder
import avatar.hardware.HardwareTypes
import avatar.hardware.types.circuitboard.CircuitBoard
import com.pi4j.context.Context
import com.pi4j.util.Console
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import runtime.setup.Configuration
import runtime.setup.Injector


/**
 * LESSON 04: Hardware structure builder. Json config parsing.
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
        println("led on")
        (avatar.body as CircuitBoard).ledOn(0, 5000L)
        delay(7000)
        (avatar.body as CircuitBoard).ledOn(0)
        (avatar.body as CircuitBoard).ledOn(1)
    }

    //add infinite loop for java app running
    coroutineScope {
        println("Start infinite main thread")
        delay(100_000_000L)
        println("End infinite main thread")
    }
}


fun init() {
    pi4j = Injector.getPi4j()
    console = Injector.getPi4jConsole()
    configuration = Injector.getRuntimeConfiguration().getConfiguration("lesson03config.json")
    avatar = AvatarBuilder(pi4j, configuration).build()
}
