package me.moeszyslak.taboo.commands

import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.Permission
import java.awt.Color

fun mimeConfiguration(configuration: Configuration) = commands("Mime Configuration") {

    command("IgnoreMime") {
        description = "Add a mime to the ignored list."
        requiredPermissionLevel = Permission.STAFF
        execute(EveryArg) {
            val mime = args.first
            val config = configuration[(guild!!.id.longValue)] ?: return@execute

            if (config.ignoredMimes.contains(mime))
                return@execute respond("$mime is already being ignored")

            config.ignoredMimes.add(mime)
            configuration.save()

            respond("$mime added to the ignore list")
        }
    }

    command("UnignoreMime") {
        description = "Remove a mime from the ignored list."
        requiredPermissionLevel = Permission.STAFF
        execute(EveryArg) {
            val mime = args.first
            val config = configuration[(guild!!.id.longValue)] ?: return@execute

            if (!config.ignoredMimes.contains(mime))
                return@execute respond("$mime is not being ignored")

            config.ignoredMimes.remove(mime)
            configuration.save()

            respond("$mime removed from the ignore list")
        }
    }

    command("IgnoredMimes") {
        description = "View all currently ignored mimes."
        requiredPermissionLevel = Permission.STAFF
        execute {
            val config = configuration[(guild!!.id.longValue)] ?: return@execute


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
    }
}