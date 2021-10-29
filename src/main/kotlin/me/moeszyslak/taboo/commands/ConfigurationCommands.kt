package me.moeszyslak.taboo.commands

import dev.kord.common.entity.Snowflake
import dev.kord.common.kColor
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.commands.commands
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.data.MimeConfiguration
import me.moeszyslak.taboo.data.Permissions
import me.moeszyslak.taboo.extensions.long
import java.awt.Color

fun configurationCommands(configuration: Configuration) = commands("Configuration") {

    command("IgnoredRoles") {
        description = "List ignored roles and ignore/unignore roles from the exclusion list"
        requiredPermission = Permissions.STAFF
        execute(
            ChoiceArg("ignore/unignore/list", "ignore", "unignore", "list").optional("list"),
            RoleArg.optionalNullable(null)
        ) {

            val (choice, role) = args
            val config = configuration[(guild.id.long())] ?: return@execute

            when (choice) {
                "ignore" -> {

                    if (role == null) {
                        respond("Received less arguments than expected. Expected: `(Role)`")
                        return@execute
                    }

                    if (config.ignoredRoles.contains(role.id.long())) {
                        respond("${role.name} is already being ignored")
                        return@execute
                    }

                    config.ignoredRoles.add(role.id.long())
                    configuration.save()

                    respond("${role.name} added to the ignore list")
                }

                "unignore" -> {

                    if (role == null) {
                        respond("Received less arguments than expected. Expected: `(Role)`")
                        return@execute
                    }

                    if (!config.ignoredRoles.contains(role.id.long())) {
                        respond("${role.name} is not being ignored")
                        return@execute
                    }

                    config.ignoredRoles.remove(role.id.long())
                    configuration.save()

                    respond("${role.name} removed from the ignore list")
                }

                "list" -> {
                    respond {
                        title = "Currently ignored roles"

                        if (config.ignoredRoles.isEmpty()) {
                            color = Color(0xE10015).kColor
                            field {
                                value = "There are currently no ignored roles."
                            }
                        } else {
                            color = Color(0xDB5F96).kColor
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

    command("Mime") {
        description = "List mimes and add/remove mimes from the ignore list"
        requiredPermission = Permissions.STAFF
        execute(
            ChoiceArg("add/remove/list", "add", "remove", "list").optional("list"),
            MultipleArg(AnyArg).optionalNullable(null)
        ) {

            val (choice, mime) = args
            val config = configuration[guild.id.long()] ?: return@execute

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
                            color = Color(0xE10015).kColor
                            field {
                                value = "There are currently no ignored mimes."
                            }
                        } else {
                            color = Color(0xDB5F96).kColor

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


    command("MimeRules") {
        description = "List mime Rules and add/remove mime rules from the ignore list"
        requiredPermission = Permissions.STAFF
        execute(
            ChoiceArg("add/remove/list", "add", "remove", "list").optional("list"),
            AnyArg.optionalNullable(null), BooleanArg.optional(false), EveryArg.optionalNullable()
        ) {

            val (choice, mime, upload, warningMessage) = args
            val config = configuration[guild.id.long()] ?: return@execute

            when (choice) {
                "add" -> {

                    if (mime == null) {
                        respond("Received less arguments than expected. Expected: `(Mime)`")
                        return@execute
                    }

                    if (warningMessage == null) {
                        respond("Received less arguments than expected. Expected: `(Text)`")
                        return@execute
                    }

                    if (config.mimeRules.containsKey(mime)) {
                        respond("$mime already has a rule attached to it.")
                        return@execute
                    }

                    config.mimeRules[mime] = MimeConfiguration(warningMessage, upload)
                    configuration.save()

                    respond("$mime's rules have been updated")
                }

                "remove" -> {

                    if (mime == null) {
                        respond("Received less arguments than expected. Expected: `(Mime)`")
                        return@execute
                    }

                    if (!config.mimeRules.containsKey(mime)) {
                        respond("$mime doesn't have a rule attached to it.")
                        return@execute
                    }

                    config.mimeRules.remove(mime)
                    configuration.save()

                    respond("$mime's rule has been deleted")
                }

                "list" -> {
                    if (config.mimeRules.isEmpty()) {
                        respond {
                            title = "Current mime rules"
                            color = Color(0xE10015).kColor
                            field {
                                value = "There are currently no mime rules"
                            }
                        }
                    } else {

                        val chunks = config.mimeRules.toList().chunked(25)

                        respondMenu {
                            chunks.map {
                                page {
                                    title = "Current mime rules"
                                    color = Color(0xDB5F96).kColor

                                    it.map { (mime, mimeconfig) ->
                                        field {
                                            name = "**$mime**"
                                            value = "```\nUpload: ${mimeconfig.uploadText}\n" +
                                                    "Delete message: ${mimeconfig.message}```"
                                        }
                                    }
                                }
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