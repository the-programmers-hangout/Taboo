package com.github.arkencl.taboo.dataclasses

import com.github.arkencl.taboo.services.PermissionLevel
import me.jakejmattson.kutils.api.dsl.data.Data

data class Configuration(var prefix: String = "",
                         val ownerId: String = "",
                         val guildConfigurations: MutableList<GuildConfiguration> = mutableListOf(GuildConfiguration()))
    : Data("config/config.json") {
    fun getGuildConfig(guildId: String) = guildConfigurations.firstOrNull { it.guildId == guildId }
}

data class GuildConfiguration(val guildId: String = "",
                              var requiredRole: String = "",
                              var sendUnfilteredFiles: PermissionLevel = PermissionLevel.STAFF)