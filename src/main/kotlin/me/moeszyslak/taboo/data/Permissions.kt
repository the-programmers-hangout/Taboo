package me.moeszyslak.taboo.data

import dev.kord.common.entity.Permission
import dev.kord.core.any
import me.jakejmattson.discordkt.api.dsl.PermissionContext
import me.jakejmattson.discordkt.api.dsl.PermissionSet
import me.moeszyslak.taboo.extensions.long

enum class Permissions : PermissionSet {
    BOT_OWNER {
        override suspend fun hasPermission(context: PermissionContext): Boolean {
            return context.discord.getInjectionObjects<Configuration>().botOwner == context.user.id.long()
        }
    },
    GUILD_OWNER {
        override suspend fun hasPermission(context: PermissionContext): Boolean {
            val guild = context.guild ?: return false
            val member = context.user.asMember(guild.id)
            return member.isOwner()
        }
    },
    ADMINISTRATOR {
        override suspend fun hasPermission(context: PermissionContext): Boolean {
            val guild = context.guild ?: return false
            val member = context.user.asMember(guild.id)
            return member.getPermissions()
                .contains(
                    Permission.Administrator
                )
        }
    },
    STAFF {
        override suspend fun hasPermission(context: PermissionContext): Boolean {
            val guild = context.guild ?: return false
            val member = context.user.asMember(guild.id)
            val configuration = context.discord.getInjectionObjects<Configuration>()
            return member.roles.any { it.id.long() == configuration[guild.id.long()]?.staffRole }
        }
    },
    NONE {
        override suspend fun hasPermission(context: PermissionContext) = true
    }
}