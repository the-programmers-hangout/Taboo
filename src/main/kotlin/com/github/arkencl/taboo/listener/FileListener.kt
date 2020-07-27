package com.github.arkencl.taboo.listener

import com.github.arkencl.taboo.service.FileUploader
import com.google.common.eventbus.Subscribe
import me.jakejmattson.kutils.api.annotations.Service
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.apache.tika.Tika

data class FileMetadata(val name: String,
                        val type: String,
                        val typeAlias: String,
                        val isAllowed: Boolean)

data class FileData(val content: String)

data class AttachmentWrapper(val fileMetadata: FileMetadata,
                             val fileData: FileData)

class FileListener() {

    @Subscribe
    fun onMessageReceived(event: GuildMessageReceivedEvent){
        val message = event.message

        if (event.author.isBot || event.message.attachments.isEmpty()) return

        val attachmentWrappers = message.attachments.map { attachmentWrapperOf(it) }
        attachmentWrappers.forEach { postProcessAttachment(event, it)}
    }

    private fun postProcessAttachment(event: GuildMessageReceivedEvent, attachmentWrapper: AttachmentWrapper) {
        val containsIllegalAttachment = !attachmentWrapper.fileMetadata.isAllowed

        if (containsIllegalAttachment) {
            event.message.delete().queue()
            val user = event.author.asMention
            val type = attachmentWrapper.fileMetadata.type
            when {
                type.startsWith("text") || attachmentWrapper.fileMetadata.typeAlias == "xml" -> {
                    event.channel.sendMessage("that seems to be text, uploading to hastebin now... <a:loading:714196361888661594>")
                            .queue() {
                                message -> message.editMessage(FileUploader().uploadFile(attachmentWrapper)).queue()
                            }
                }
                else -> event.channel.sendMessage(responseFor(user, attachmentWrapper.fileMetadata)).queue()
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

    private fun attachmentWrapperOf(attachment: Message.Attachment): AttachmentWrapper {
        return AttachmentWrapper(metadataOf(attachment), getContent(attachment))
    }

    private fun commonAliasFor(type: String): String {
        return when {
            type == "application/x-tika-ooxml" -> "documents"
            type == "application/pdf" -> "pdf files"
            type == "application/xml" -> "xml"
            type.startsWith("application") -> "binaries"
            type.startsWith("text") -> "documents or code"
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