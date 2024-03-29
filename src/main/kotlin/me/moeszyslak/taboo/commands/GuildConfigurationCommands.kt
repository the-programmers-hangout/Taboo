package me.moeszyslak.taboo.commands

import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.IntegerArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.commands.commands
import me.moeszyslak.taboo.conversations.ConfigurationConversation
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.data.Permissions
import me.moeszyslak.taboo.extensions.long

fun guildConfigurationCommands(configuration: Configuration) = commands("GuildConfiguration") {

    command("Setup") {
        description = "Setup a guild to use Taboo"
        requiredPermission = Permissions.ADMINISTRATOR
        execute {
            if (configuration.hasGuildConfig(guild.id.long())) {
                respond("Guild configuration already exists. You can use commands to modify the config")
                return@execute
            }

            ConfigurationConversation(configuration)
                .createConfigurationConversation(guild)
                    .startPublicly(discord, author, channel)

            respond("${guild.name} has been setup")
        }
    }


    command("Prefix") {
        description = "Set the prefix required for the bot to register a command."
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Prefix")) {
            val prefix = args.first
            val config = configuration[guild.id.long()] ?: return@execute

            config.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    command("StaffRole") {
        description = "Set the role required to use this bot."
        requiredPermission = Permissions.STAFF
        execute(RoleArg) {
            val requiredRole = args.first
            val config = configuration[(guild.id.long())] ?: return@execute

            config.staffRole = requiredRole.id.long()
            configuration.save()

            respond("Required role set to ${requiredRole.name}")
        }
    }

    command("LogChannel") {
        description = "Set the channel where logs will be output."
        requiredPermission = Permissions.STAFF
        execute(ChannelArg) {
            val logChannel = args.first
            val config = configuration[(guild.id.long())] ?: return@execute

            config.logChannel = logChannel.id.long()
            configuration.save()

            respond("Logging channel set to ${logChannel.name}")
        }
    }

    command("LineLimit") {
        description = "Set the max line count before upload."
        execute(IntegerArg) {
            val limit = args.first
            val config = configuration[guild.id.long()] ?: return@execute

            config.lineLimit = limit
            configuration.save()

            respond("Line limit set to $limit")
        }
    }
}