package me.moeszyslak.taboo.commands

import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.commands.commands
import me.moeszyslak.taboo.conversations.ConfigurationConversation
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.data.Permissions

fun guildConfigurationCommands(configuration: Configuration) = commands("GuildConfiguration") {

    guildCommand("Setup") {
        description = "Setup a guild to use Taboo"
        requiredPermission = Permissions.ADMINISTRATOR
        execute {
            if (configuration.hasGuildConfig(guild.id.value)) {
                respond("Guild configuration already exists. You can use commands to modify the config")
                return@execute
            }

            ConfigurationConversation(configuration)
                    .createConfigurationConversation(guild)
                    .startPublicly(discord, author, channel)

            respond("${guild.name} has been setup")
        }
    }


    guildCommand("Prefix") {
        description = "Set the prefix required for the bot to register a command."
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Prefix")) {
            val prefix = args.first
            val config = configuration[guild.id.value] ?: return@execute

            config.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    guildCommand("StaffRole") {
        description = "Set the role required to use this bot."
        requiredPermission = Permissions.STAFF
        execute(RoleArg) {
            val requiredRole = args.first
            val config = configuration[(guild.id.value)] ?: return@execute

            config.staffRole = requiredRole.id.value
            configuration.save()

            respond("Required role set to ${requiredRole.name}")
        }
    }

    guildCommand("LogChannel") {
        description = "Set the channel where logs will be output."
        requiredPermission = Permissions.STAFF
        execute(ChannelArg) {
            val logChannel = args.first
            val config = configuration[(guild.id.value)] ?: return@execute

            config.logChannel = logChannel.id.value
            configuration.save()

            respond("Logging channel set to ${logChannel.name}")
        }
    }
}