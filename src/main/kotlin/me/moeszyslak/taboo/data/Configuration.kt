package me.moeszyslak.taboo.data

import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.core.entity.channel.Channel
import me.jakejmattson.discordkt.api.dsl.Data

data class Configuration(
        val botOwner: Long = 345541952500006912,
        val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()) : Data("config/config.json") {

    operator fun get(id: Long) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Long) = guildConfigurations.containsKey(guildId)

    fun setup(guild: Guild, prefix: String, logChannel: Channel, staffRole: Role) {
        if (guildConfigurations[guild.id.longValue] != null) return

        val newConfiguration = GuildConfiguration(
                logChannel.id.longValue,
                prefix,
                staffRole.id.longValue,
                mutableSetOf(),
                mutableSetOf()
        )

        guildConfigurations[guild.id.longValue] = newConfiguration
        save()
    }
}

data class GuildConfiguration(
        var logChannel: Long,
        var prefix: String,
        var staffRole: Long,
        var ignoredRoles: MutableSet<Long>,
        var ignoredMimes: MutableSet<String>
)
