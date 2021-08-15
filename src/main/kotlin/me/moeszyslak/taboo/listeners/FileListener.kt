package me.moeszyslak.taboo.listeners

import dev.kord.core.event.message.MessageCreateEvent
import me.jakejmattson.discordkt.api.dsl.listeners
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.services.FileTypeService
import me.moeszyslak.taboo.services.FileUploader
import me.moeszyslak.taboo.services.PermissionsService

fun fileListener(configuration: Configuration,
                 permissionsService: PermissionsService,
                 fileTypeService: FileTypeService,
                 fileUploader: FileUploader) = listeners {
    on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (guildId == null) return@on
        if (!configuration.hasGuildConfig(guildId!!)) return@on
        if (permissionsService.canSendFile(message.getAuthorAsMember()!!)) return@on

        if (message.attachments.isNotEmpty()) {
            fileTypeService.handleMessage(message)
        } else if (message.content.lines().size >= configuration[guildId!!]!!.lineLimit) {
            message.delete()
            message.channel.createMessage(
                fileUploader.upload(message.content).fold(
                    success = { "Large text from ${message.author?.mention} uploaded to pastecord: $it" },
                    failure = { "Large text from ${message.author?.mention} failed to upload to pastecord: ${it.localizedMessage}" }
                )
            )
        }
    }
}