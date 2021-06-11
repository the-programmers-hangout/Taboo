package me.moeszyslak.taboo.commands

import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.taboo.conversations.ConfigurationConversation
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.Permission

fun guildConfigurationCommands(configuration: Configuration) = commands("GuildConfiguration") {
    guildCommand("Setup") {
        description = "Setup a guild to use Taboo"
        requiredPermissionLevel = Permission.GUILD_OWNER
        execute {
            if (configuration.hasGuildConfig(guild.id)) {
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
        requiredPermissionLevel = Permission.STAFF
        execute(AnyArg("Prefix")) {
            val prefix = args.first
            val config = configuration[guild.id] ?: return@execute

            config.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    guildCommand("StaffRole") {
        description = "Set the role required to use this bot."
        requiredPermissionLevel = Permission.STAFF
        execute(RoleArg) {
            val requiredRole = args.first
            val config = configuration[guild.id] ?: return@execute

            config.staffRole = requiredRole.id
            configuration.save()

            respond("Required role set to ${requiredRole.name}")
        }
    }

    guildCommand("LogChannel") {
        description = "Set the channel where logs will be output."
        requiredPermissionLevel = Permission.STAFF
        execute(ChannelArg) {
            val logChannel = args.first
            val config = configuration[guild.id] ?: return@execute

            config.logChannel = logChannel.id
            configuration.save()

            respond("Logging channel set to ${logChannel.name}")
        }

        guildCommand("LineLimit") {
            description = "Set the max line count before upload."
            requiredPermissionLevel = Permission.STAFF
            execute(IntegerArg) {
                val limit = args.first
                val config = configuration[guild.id] ?: return@execute

                config.lineLimit = limit
                configuration.save()

                respond("Line limit set to $limit")
            }
        }
    }
}