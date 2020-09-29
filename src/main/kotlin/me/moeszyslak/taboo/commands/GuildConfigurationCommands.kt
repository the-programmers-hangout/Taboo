package me.moeszyslak.taboo.commands

import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.services.ConversationService
import me.moeszyslak.taboo.conversations.ConfigurationConversation
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.Permission

fun guildConfigurationCommands(configuration: Configuration, conversationService: ConversationService) = commands("GuildConfiguration") {

    command("Setup") {
        description = "Setup a guild to use Taboo"
        requiredPermissionLevel = Permission.GUILD_OWNER
        execute {
            if (configuration.hasGuildConfig(guild!!.id.longValue))
                return@execute respond("Guild configuration already exists. You can use commands to modify the config")

            conversationService.startPublicConversation<ConfigurationConversation>(author, channel.asChannel(), guild!!)
            respond("${guild!!.name} has been setup")
        }
    }


    command("Prefix") {
        description = "Set the prefix required for the bot to register a command."
        requiredPermissionLevel = Permission.STAFF
        execute(AnyArg("Prefix")) {
            val prefix = args.first
            val config = configuration[guild!!.id.longValue] ?: return@execute

            config.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    command("StaffRole") {
        description = "Set the role required to use this bot."
        requiredPermissionLevel = Permission.STAFF
        execute(RoleArg) {
            val requiredRole = args.first
            val config = configuration[(guild!!.id.longValue)] ?: return@execute

            config.staffRole = requiredRole.id.longValue
            configuration.save()

            respond("Required role set to ${requiredRole.name}")
        }
    }

    command("LogChannel") {
        description = "Set the channel where logs will be output."
        requiredPermissionLevel = Permission.STAFF
        execute(ChannelArg) {
            val logChannel = args.first
            val config = configuration[(guild!!.id.longValue)] ?: return@execute

            config.staffRole = logChannel.id.longValue
            configuration.save()

            respond("Required role set to ${logChannel.name}")
        }
    }
}