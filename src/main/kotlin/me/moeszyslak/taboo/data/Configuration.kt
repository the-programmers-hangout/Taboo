package me.moeszyslak.taboo.data

import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.Channel
import me.jakejmattson.discordkt.api.dsl.Data

data class Configuration(
        val botOwner: Long = 345541952500006912,
        val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()) : Data("config/config.json") {

    operator fun get(id: Long) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Long) = guildConfigurations.containsKey(guildId)

    fun setup(guild: Guild, prefix: String, logChannel: Channel, staffRole: Role) {
        if (guildConfigurations[guild.id.value] != null) return

        val newConfiguration = GuildConfiguration(
            logChannel.id.value,
            prefix,
            staffRole.id.value,
            mutableSetOf(),
            mutableSetOf(),
            mutableMapOf(),
            4000
        )

        guildConfigurations[guild.id.value] = newConfiguration
        save()
    }
}

data class GuildConfiguration(
    var logChannel: Long,
    var prefix: String,
    var staffRole: Long,
    var ignoredRoles: MutableSet<Long>,
    var ignoredMimes: MutableSet<String>,
    var mimeRules: MutableMap<String, MimeConfiguration>,
    var lineLimit: Int
)

data class MimeConfiguration(
        var message: String,
        var uploadText: Boolean
)