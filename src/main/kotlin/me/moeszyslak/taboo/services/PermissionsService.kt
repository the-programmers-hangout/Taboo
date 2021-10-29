package me.moeszyslak.taboo.services

import dev.kord.core.entity.Member
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.data.Permissions
import me.moeszyslak.taboo.extensions.long

@Service
class PermissionsService(private val configuration: Configuration, private val discord: Discord) {
    suspend fun canSendFile(member: Member) =
        discord.permissions.hasPermission(discord, member, Permissions.STAFF) || member.isIgnored()


    private fun Member.isIgnored(): Boolean {
        val config = configuration[guildId.long()] ?: return false

        return config.ignoredRoles.intersect(roleIds).isNotEmpty()
    }
}