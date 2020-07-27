package com.github.arkencl.taboo.service

import com.github.arkencl.taboo.data.BotConfiguration
import me.jakejmattson.kutils.api.annotations.Service

enum class PermissionLevel {
    Everyone,
    Staff,
    Administrator,
    GuildOwner,
    BotOwner
}

var DEFAULT_REQUIRED_PERMISSION = PermissionLevel.Staff

@Service
class PermissionService(private val botConfiguration: BotConfiguration){

}
