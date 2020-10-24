package me.moeszyslak.taboo.commands

import com.gitlab.kordlib.common.entity.Snowflake
import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChoiceArg
import me.jakejmattson.discordkt.api.arguments.MultipleArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.Permission
import java.awt.Color

fun configurationCommands(configuration: Configuration) = commands("Configuration") {

    guildCommand("IgnoredRoles") {
        description = "List ignored roles and ignore/unignore roles from the exclusion list"
        requiredPermissionLevel = Permission.STAFF
        execute(ChoiceArg("ignore/unignore/list", "ignore", "unignore", "list").makeOptional("list"),
                RoleArg.makeNullableOptional(null)) {

            val (choice, role) = args
            val config = configuration[(guild.id.longValue)] ?: return@execute

            when (choice) {
                "ignore" -> {

                    if (role == null) {
                        respond("Received less arguments than expected. Expected: `(Role)`")
                        return@execute
                    }

                    if (config.ignoredRoles.contains(role.id.longValue)) {
                        respond("${role.name} is already being ignored")
                        return@execute
                    }

                    config.ignoredRoles.add(role.id.longValue)
                    configuration.save()

                    respond("${role.name} added to the ignore list")
                }

                "unignore" -> {

                    if (role == null) {
                        respond("Received less arguments than expected. Expected: `(Role)`")
                        return@execute
                    }

                    if (!config.ignoredRoles.contains(role.id.longValue)) {
                        respond("${role.name} is not being ignored")
                        return@execute
                    }

                    config.ignoredRoles.remove(role.id.longValue)
                    configuration.save()

                    respond("${role.name} removed from the ignore list")
                }

                "list" -> {
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
                                guild.getRole(Snowflake(ignoredRole)).mention
                            }

                            field {
                                value = roles.joinToString("\n")
                            }
                        }

                    }
                }

                else -> {
                    respond("Invalid choice")
                }
            }
        }
    }

    guildCommand("Mime") {
        description = "List mimes and add/remove mimes from the ignore list"
        requiredPermissionLevel = Permission.STAFF
        execute(ChoiceArg("add/remove/list", "add", "remove", "list").makeOptional("list"),
                MultipleArg(AnyArg).makeNullableOptional(null)) {

            val (choice, mime) = args
            val config = configuration[guild.id.longValue] ?: return@execute

            when (choice) {
                "add" -> {

                    if (mime == null) {
                        respond("Received less arguments than expected. Expected: `(Mime)`")
                        return@execute
                    }

                    mime.forEach {
                        if (config.ignoredMimes.contains(it)) {
                            respond("$it is already being ignored")
                            return@execute
                        }
                    }

                    mime.forEach { config.ignoredMimes.add(it) }
                    configuration.save()

                    respond("${mime.joinToString()} added to the ignore list")
                }

                "remove" -> {

                    if (mime == null) {
                        respond("Received less arguments than expected. Expected: `(Mime)`")
                        return@execute
                    }

                    mime.forEach {
                        if (!config.ignoredMimes.contains(it)) {
                            respond("$it is not being ignored")
                            return@execute
                        }
                    }

                    mime.forEach { config.ignoredMimes.remove(it) }
                    configuration.save()

                    respond("${mime.joinToString()} removed to the ignore list")
                }

                "list" -> {
                    respond {
                        title = "Currently ignored mimes"

                        if (config.ignoredMimes.isEmpty()) {
                            color = Color(0xE10015)
                            field {
                                value = "There are currently no ignored mimes."
                            }
                        } else {
                            color = Color(0xDB5F96)

                            field {
                                value = config.ignoredMimes.joinToString("\n")
                            }
                        }

                    }
                }

                else -> {
                    respond("Invalid choice")
                }
            }
        }
    }
}