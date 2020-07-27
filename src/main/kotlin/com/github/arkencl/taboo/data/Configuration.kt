package com.github.arkencl.taboo.data

import com.google.gson.Gson
import me.jakejmattson.kutils.api.dsl.data.Data
import java.io.File

data class BotConfiguration(val token: String = "",
                            var prefix: String = "taboo!",
                            val ownerId: String = "") : Data("config/config.json")

fun loadConfig(initialConfig: (BotConfiguration?) -> Unit) {
    val configFile = File("config/config.json")

    if(!configFile.exists()) {
        return initialConfig(null)
    }

    return initialConfig(Gson().fromJson(configFile.readText(), BotConfiguration::class.java))
}