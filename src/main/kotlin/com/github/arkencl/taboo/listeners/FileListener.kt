package com.github.arkencl.taboo.listeners

import com.github.arkencl.taboo.dataclasses.Configuration
import com.github.arkencl.taboo.dataclasses.FileData
import com.github.arkencl.taboo.dataclasses.FileMetadata
import com.github.arkencl.taboo.dataclasses.FileWrapper
import com.github.arkencl.taboo.locale.Description
import com.github.arkencl.taboo.services.FileUploader
import com.github.arkencl.taboo.services.PermissionLevel
import com.github.arkencl.taboo.services.PermissionsService
import com.google.common.eventbus.Subscribe
import me.jakejmattson.discordkt.api.annotations.Service
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.apache.tika.Tika

enum class CommonAlias(val alias: String) {
    DOCUMENT("text/document+code")
}

class FileListener (private val configuration: Configuration, private val permissionsService: PermissionsService) {

    private val logger = KotlinLogging.logger {}

    @Subscribe
    fun onMessageReceived(event: GuildMessageReceivedEvent){
        logger.info { "1" }
        val guildConfiguration = configuration.getGuildConfig(event.guild.id) ?: return
        logger.info { "2" }
        val message = event.message
        logger.info { "3" }
        if (event.author.isBot || event.message.attachments.isEmpty()) return
        logger.info { "4" }
        if (permissionsService.hasClearance(event.member!!, PermissionLevel.valueOf(guildConfiguration.sendUnfilteredFiles))) return
        logger.info { "5" }
        val attachmentWrappers = message.attachments.map { attachmentWrapperOf(it) }
        logger.info { "6" }
        attachmentWrappers.forEach { postProcessAttachment(event, it)}
        logger.info { "7" }
    }

    private fun postProcessAttachment(event: GuildMessageReceivedEvent, fileWrapper: FileWrapper) {
        val containsIllegalAttachment = !fileWrapper.fileMetadata.isAllowed

        if (containsIllegalAttachment) {
            event.message.delete().queue()
            val user = event.author.asMention
            val type = fileWrapper.fileMetadata.typeAlias
            when {
                type.startsWith("text") -> {
                    event.channel.sendMessage("that seems to be text, uploading to hasteb.in now... <a:loading:714196361888661594>")
                            .queue {
                                message -> message.editMessage(FileUploader().uploadFile(fileWrapper)).queue()
                            }
                }
                else -> {
                    val guildConfiguration = configuration.getGuildConfig(event.guild.id) ?: return
                    event.guild.getTextChannelById(guildConfiguration.logChannel)!!
                            .sendMessage(Description.INVALID_FILE_TYPE + fileWrapper.fileMetadata.name
                                    + " of type ${fileWrapper.fileMetadata.type}"
                                    + " by member ${event.author.asMention} in ${event.channel.asMention}").queue()
                    event.channel.sendMessage(responseFor(user, fileWrapper.fileMetadata)).queue()
                }
            }
        }
    }

    private fun responseFor(author: String, fileMetadata: FileMetadata): String {
        return "Please don't send ${fileMetadata.typeAlias} here $author."
    }


    private fun metadataOf(attachment: Message.Attachment): FileMetadata {
        val stream = attachment.retrieveInputStream().get()
        val type = Tika().detect(stream)
        stream.close()
        return FileMetadata(attachment.fileName, type, commonAliasFor(type), isAllowed(type))
    }

    private fun attachmentWrapperOf(attachment: Message.Attachment): FileWrapper {
        return FileWrapper(metadataOf(attachment), getContent(attachment))
    }

    private fun commonAliasFor(type: String): String {
        return when {
            type == "application/x-tika-ooxml" -> "documents"
            type == "application/pdf" -> "pdf files"
            type == "application/xml" -> CommonAlias.DOCUMENT.alias
            type == "application/json" -> CommonAlias.DOCUMENT.alias
            type == "application/ld+json" -> CommonAlias.DOCUMENT.alias
            type == "application/xhtml+xml" -> CommonAlias.DOCUMENT.alias
            type.startsWith("application") -> "binaries"
            type.startsWith("text") -> CommonAlias.DOCUMENT.alias
            else -> "those types of files"
        }
    }

    private fun isAllowed (type: String): Boolean {
        return type.startsWith("image")
                || type.startsWith("video")
    }

    private fun getContent(attachment: Message.Attachment): FileData {
        return FileData(attachment.retrieveInputStream()
                .get()
                .bufferedReader()
                .use { it.readText() })
    }

}