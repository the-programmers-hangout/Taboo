package me.moeszyslak.taboo.commands

import com.gitlab.kordlib.common.entity.Snowflake
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.Permission
import java.awt.Color

fun roleCommands(configuration: Configuration) = commands("Roles Configuration") {
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