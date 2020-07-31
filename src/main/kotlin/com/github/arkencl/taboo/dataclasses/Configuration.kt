package com.github.arkencl.taboo.dataclasses

import com.google.gson.Gson
import me.jakejmattson.discordkt.api.dsl.data.Data
import java.io.File

data class Configuration(val token: String = "",
                         var prefix: String = "",
                         val ownerId: String = "",
                         val guildConfigurations: MutableList<GuildConfiguration> = mutableListOf(GuildConfiguration()))
    : Data("config/config.json") {
    fun getGuildConfig(guildId: String) = guildConfigurations.firstOrNull { it.guildId == guildId }
}

data class GuildConfiguration(val guildId: String = "insert-your-id",
                              var requiredRole: String = "",
                              var sendUnfilteredFiles: String = "",
                              var logChannel: String = "")

fun loadConfig(onFinishedLoading: (Configuration?) -> Unit) {
    val configFile = File("config/config.json")

    if(!configFile.exists()) {
        return onFinishedLoading(null)
    }

    return onFinishedLoading(Gson().fromJson(configFile.readText(), Configuration::class.java))
}