package me.moeszyslak.taboo.data

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.Channel
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private val file = File("config/config.json")

@Serializable
data class Configuration(
    val botOwner: Long = 345541952500006912,
    val guildConfigurations: MutableMap<Snowflake, GuildConfiguration> = mutableMapOf()) {

    operator fun get(id: Snowflake) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId)

    fun setup(guild: Guild, prefix: String, logChannel: Channel, staffRole: Role) {
        if (hasGuildConfig(guild.id)) return

        guildConfigurations[guild.id] = GuildConfiguration(prefix, logChannel.id, staffRole.id)
        save()
    }

    fun save() {
        file.writeText(Json.encodeToString(this))
    }

    companion object {
        @JvmStatic
        fun load() =
            if (file.exists())
                Json.decodeFromString(file.readText())
            else {
                val parent = file.parentFile

                if (parent != null && !parent.exists())
                    parent.mkdirs()

                Configuration().apply { save() }
            }
    }
}

@Serializable
data class GuildConfiguration(
    var prefix: String,
    var logChannel: Snowflake,
    var staffRole: Snowflake,
    var lineLimit: Int = 20,
    var ignoredRoles: MutableSet<Long> = mutableSetOf(),
    var ignoredMimes: MutableSet<String> = mutableSetOf(),
    var mimeRules: MutableMap<String, MimeConfiguration> = mutableMapOf()
)

@Serializable
data class MimeConfiguration(
    var message: String,
    var uploadText: Boolean
)