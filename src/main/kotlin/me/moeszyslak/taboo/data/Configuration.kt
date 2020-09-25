package me.moeszyslak.taboo.data

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.api.dsl.Data

data class Configuration(
        val botOwner: Long = 345541952500006912,
        val guildConfigurations: MutableList<GuildConfiguration> = mutableListOf(GuildConfiguration())

) : Data("config/config.json", killIfGenerated = false) {
        fun hasGuildConfig(guildId: Long) = getGuildConfig(guildId) != null
        fun getGuildConfig(guildId: Long) = guildConfigurations.firstOrNull { it.guildID == guildId }
}

data class GuildConfiguration(
        var guildID: Long = 699595207720566824,
        var logChannel: Long = 735931902359502888,
        var prefix: String = "+",
        var staffRole: String = "staff_role_name",
        var ignoredRoles: List<String> = listOf("role name 1", "role name 2")
)
