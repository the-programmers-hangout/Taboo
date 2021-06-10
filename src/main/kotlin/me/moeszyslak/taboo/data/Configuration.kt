package me.moeszyslak.taboo.data

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.core.entity.channel.Channel
import me.jakejmattson.discordkt.api.dsl.Data

data class Configuration(
    val botOwner: Long = 345541952500006912,
    val guildConfigurations: MutableMap<Snowflake, GuildConfiguration> = mutableMapOf()) : Data("config/config.json") {

    operator fun get(id: Snowflake) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId)

    fun setup(guild: Guild, prefix: String, logChannel: Channel, staffRole: Role) {
        if (hasGuildConfig(guild.id)) return

        guildConfigurations[guild.id] = GuildConfiguration(prefix, logChannel.id, staffRole.id)
        save()
    }
}

data class GuildConfiguration(
    var prefix: String,
    var logChannel: Snowflake,
    var staffRole: Snowflake,
    var ignoredRoles: MutableSet<Long> = mutableSetOf(),
    var ignoredMimes: MutableSet<String> = mutableSetOf(),
    var mimeRules: MutableMap<String, MimeConfiguration> = mutableMapOf()
)

data class MimeConfiguration(
    var message: String,
    var uploadText: Boolean
)