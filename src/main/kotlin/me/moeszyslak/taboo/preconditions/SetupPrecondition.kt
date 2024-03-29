package me.moeszyslak.taboo.preconditions

import me.jakejmattson.discordkt.api.dsl.precondition
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.long


fun setupPrecondition(configuration: Configuration) = precondition {
    val command = command ?: return@precondition fail()
    val guild = guild ?: return@precondition fail()

    if (configuration.hasGuildConfig(guild.id.long())) return@precondition

    if (!command.names.any { it.toLowerCase() == "setup" })
        fail("You must first use the `Setup` command in this guild.")
}