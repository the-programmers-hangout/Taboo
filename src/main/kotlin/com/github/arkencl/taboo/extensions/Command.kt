package com.github.arkencl.taboo.extensions

import com.github.arkencl.taboo.services.DEFAULT_REQUIRED_PERMISSION
import com.github.arkencl.taboo.services.PermissionLevel
import me.jakejmattson.kutils.api.dsl.command.Command
import me.jakejmattson.kutils.api.dsl.command.CommandEvent
import java.util.*

fun CommandEvent<*>.reactSuccess() = message.addReaction("✅").queue()

private object CommandPropertyStore {
    val permissions = WeakHashMap<Command, PermissionLevel>()
}

var Command.requiredPermissionLevel: PermissionLevel
    get() = CommandPropertyStore.permissions[this] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        CommandPropertyStore.permissions[this] = value
    }