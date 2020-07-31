package com.github.arkencl.taboo.preconditions

import com.github.arkencl.taboo.extensions.requiredPermissionLevel
import com.github.arkencl.taboo.locale.ErrorMessage
import com.github.arkencl.taboo.services.PermissionLevel
import com.github.arkencl.taboo.services.PermissionsService
import me.jakejmattson.discordkt.api.dsl.command.CommandEvent
import me.jakejmattson.discordkt.api.dsl.preconditions.Fail
import me.jakejmattson.discordkt.api.dsl.preconditions.Pass
import me.jakejmattson.discordkt.api.dsl.preconditions.Precondition
import me.jakejmattson.discordkt.api.dsl.preconditions.PreconditionResult
import me.jakejmattson.discordkt.api.extensions.jda.toMember

class PermissionPrecondition(private val permissionsService: PermissionsService) : Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Fail()
        val requiredPermissionLevel = command.requiredPermissionLevel
        val guild = event.guild!!
        val member = event.author.toMember(guild)!!

        val response = when (requiredPermissionLevel) {
            PermissionLevel.BOT_OWNER -> ErrorMessage.MISSING_CLEARANCE + " You must be the bot owner."
            PermissionLevel.GUILD_OWNER -> ErrorMessage.MISSING_CLEARANCE + " You must be the guild owner."
            else -> ""
        }

        if (!permissionsService.hasClearance(member, requiredPermissionLevel))
            return Fail(response)

        return Pass
    }

}