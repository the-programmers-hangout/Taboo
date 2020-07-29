package com.github.arkencl.taboo.services

import com.github.arkencl.taboo.dataclasses.Configuration
import me.jakejmattson.kutils.api.annotations.Service
import net.dv8tion.jda.api.entities.Member

enum class PermissionLevel {
    NONE,
    STAFF,
    GUILD_OWNER,
    BOT_OWNER
}

var DEFAULT_REQUIRED_PERMISSION = PermissionLevel.STAFF

@Service
class PermissionsService(private val configuration: Configuration) {
    fun hasClearance(member: Member, requiredPermissionLevel: PermissionLevel) = member.getPermissionLevel().ordinal <= requiredPermissionLevel.ordinal

    private fun Member.getPermissionLevel() =
            when {
                isBotOwner() -> PermissionLevel.BOT_OWNER
                isGuildOwner() -> PermissionLevel.GUILD_OWNER
                isStaff() -> PermissionLevel.STAFF
                else -> PermissionLevel.NONE
            }

    private fun Member.isBotOwner() = user.id == configuration.ownerId

    private fun Member.isGuildOwner() = isOwner

    private fun Member.isStaff(): Boolean {
        val guildConfig = configuration.getGuildConfig(guild.id) ?: return false
        val requiredRoleName = guildConfig.requiredRole
        val requiredRole = guild.getRolesByName(requiredRoleName, true).firstOrNull() ?: return false

        return requiredRole in roles
    }
}