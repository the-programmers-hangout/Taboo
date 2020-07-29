package com.github.arkencl.taboo.listeners

import com.github.arkencl.taboo.dataclasses.*
import com.github.arkencl.taboo.services.FileUploader
import com.github.arkencl.taboo.services.PermissionsService
import com.google.common.eventbus.Subscribe
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.apache.tika.Tika

enum class CommonAlias(val alias: String) {
    DOCUMENT("text/document+code")
}

class FileListener(private val configuration: Configuration, private val permissionsService: PermissionsService) {

    @Subscribe
    fun onMessageReceived(event: GuildMessageReceivedEvent){
        val guildConfiguration = configuration.getGuildConfig(event.guild.id) ?: return

        val message = event.message

        if (event.author.isBot || event.message.attachments.isEmpty()) return

        if (permissionsService.hasClearance(event.member!!, guildConfiguration.sendUnfilteredFiles)) return

        val attachmentWrappers = message.attachments.map { attachmentWrapperOf(it) }
        attachmentWrappers.forEach { postProcessAttachment(event, it)}
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
                else -> event.channel.sendMessage(responseFor(user, fileWrapper.fileMetadata)).queue()
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