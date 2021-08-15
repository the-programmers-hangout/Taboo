package me.moeszyslak.taboo.conversations

import dev.kord.core.entity.Guild
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.conversations.conversation
import me.moeszyslak.taboo.data.Configuration

class ConfigurationConversation(private val configuration: Configuration) {
    fun createConfigurationConversation(guild: Guild) = conversation {
        val prefix = prompt(EveryArg, "Bot prefix:")
        val log = prompt(ChannelArg, "Log channel:")
        val staffRole = prompt(RoleArg, "Staff role:")

        configuration.setup(guild, prefix, log, staffRole)
    }
}