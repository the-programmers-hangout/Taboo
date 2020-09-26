package me.moeszyslak.taboo.preconditions

import me.jakejmattson.discordkt.api.dsl.*
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.Permission


class SetupPrecondition(private val configuration: Configuration) : Precondition() {
    override suspend fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Fail()
        val guild = event.guild!!

        if (!command.names.contains("Setup")) {
            if (!configuration.hasGuildConfig(guild.id.longValue))
                return Fail("You must first use the `Setup` command in this guild.")
        }

        return Pass
    }
}