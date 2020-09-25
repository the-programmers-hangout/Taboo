package me.moeszyslak.taboo.commands

import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.Permission

fun guildConfigurationCommands(configuration: Configuration) = commands("GuildConfiguration") {

    command("SetPrefix") {
        description = "Set the prefix required for the bot to register a command."
        requiredPermissionLevel = Permission.GUILD_OWNER
        execute(AnyArg("Prefix")) {
            val prefix = args.first
            val config = configuration.getGuildConfig(guild!!.id.longValue) ?: return@execute

            config.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    command("SetRole") {
        description = "Set the role required to use this bot."
        requiredPermissionLevel = Permission.GUILD_OWNER
        execute(RoleArg) {
            val requiredRole = args.first.name
            val config = configuration.getGuildConfig(guild!!.id.longValue) ?: return@execute

            config.staffRole = requiredRole
            configuration.save()

            respond("Required role set to $requiredRole")
        }
    }
}