package me.moeszyslak.taboo.commands

import com.gitlab.kordlib.common.entity.Snowflake
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.services.ConversationService
import me.moeszyslak.taboo.conversations.ConfigurationConversation
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.Permission
import java.awt.Color

fun guildConfigurationCommands(configuration: Configuration, conversationService: ConversationService, discord: Discord) = commands("GuildConfiguration") {

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

    command("IgnoreRole") {
        description = "Add a role to the ignored list."
        requiredPermissionLevel = Permission.STAFF
        execute(RoleArg) {
            val role = args.first
            val config = configuration[(guild!!.id.longValue)] ?: return@execute

            if (config.ignoredRoles.contains(role.id.longValue))
                return@execute respond("${role.name} is already being ignored")

            config.ignoredRoles.add(role.id.longValue)
            configuration.save()

            respond("${role.name} added to the ignore list")
        }
    }

    command("UnignoreRole") {
        description = "Remove a role from the ignored list."
        requiredPermissionLevel = Permission.STAFF
        execute(RoleArg) {
            val role = args.first
            val config = configuration[(guild!!.id.longValue)] ?: return@execute

            if (!config.ignoredRoles.contains(role.id.longValue))
                return@execute respond("${role.name} is not being ignored")

            config.ignoredRoles.remove(role.id.longValue)
            configuration.save()

            respond("${role.name} removed from the ignore list")
        }
    }

    command("IgnoredRoles") {
        description = "View all currently ignored roles."
        requiredPermissionLevel = Permission.STAFF
        execute {
            val config = configuration[(guild!!.id.longValue)] ?: return@execute


            respond {
                title = "Currently ignored roles"

                if (config.ignoredRoles.isEmpty()) {
                    color = Color(0xE10015)
                    field {
                        value = "There are currently no ignored roles."
                    }
                } else {
                    color = Color(0xDB5F96)
                    val roles = config.ignoredRoles.map { ignoredRole ->
                        guild!!.getRole(Snowflake(ignoredRole)).mention
                    }

                    field {
                        value = roles.joinToString("\n")
                    }
                }

            }

        }
    }
}