package me.moeszyslak.taboo.data

import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.Channel
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.api.dsl.Data
import me.moeszyslak.taboo.extensions.long

@Serializable
data class Configuration(
    val botOwner: Long = 345541952500006912,
    val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()
) : Data() {

    operator fun get(id: Long) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Long) = guildConfigurations.containsKey(guildId)

    fun setup(guild: Guild, prefix: String, logChannel: Channel, staffRole: Role) {
        if (guildConfigurations[guild.id.long()] != null) return

        val newConfiguration = GuildConfiguration(
            logChannel.id.long(),
            prefix,
            staffRole.id.long(),
            mutableSetOf(),
            mutableSetOf(),
            mutableMapOf(),
            4000
        )

        guildConfigurations[guild.id.long()] = newConfiguration
        save()
    }
}

@Serializable
data class GuildConfiguration(
    var logChannel: Long,
    var prefix: String,
    var staffRole: Long,
    var ignoredRoles: MutableSet<Long>,
    var ignoredMimes: MutableSet<String>,
    var mimeRules: MutableMap<String, MimeConfiguration>,
    var lineLimit: Int
)

@Serializable
data class MimeConfiguration(
    var message: String,
    var uploadText: Boolean
)