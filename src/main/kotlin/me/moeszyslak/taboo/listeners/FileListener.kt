package me.moeszyslak.taboo.listeners

import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import me.jakejmattson.discordkt.api.dsl.listeners
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.services.FileTypeService
import me.moeszyslak.taboo.services.PermissionsService

fun fileListener(fileTypeService: FileTypeService, configuration: Configuration, permissionsService: PermissionsService) = listeners {
    on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (message.attachments.isEmpty()) return@on
        if (permissionsService.canSendFile(message.getAuthorAsMember()!!)) return@on

        fileTypeService.handleMessage(message)
    }
}