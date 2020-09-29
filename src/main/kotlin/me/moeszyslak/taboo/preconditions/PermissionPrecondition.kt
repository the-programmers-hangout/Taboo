package me.moeszyslak.taboo.preconditions

import me.jakejmattson.discordkt.api.dsl.*
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.PermissionsService


class PermissionPrecondition(private val permissionsService: PermissionsService) : Precondition() {
    override suspend fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Fail()
        val requiredPermissionLevel = command.requiredPermissionLevel
        val guild = event.guild!!
        val member = event.author.asMember(guild.id)


        if (!permissionsService.hasClearance(member, requiredPermissionLevel))
            return Fail()

        return Pass
    }
}