package me.moeszyslak.taboo.services

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.entity.Attachment
import com.gitlab.kordlib.core.entity.Message
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.data.FileData
import me.moeszyslak.taboo.data.FileMetadata
import me.moeszyslak.taboo.data.FileWrapper
import org.apache.tika.Tika
import java.io.ByteArrayInputStream

enum class CommonAlias(val alias: String) {
    DOCUMENT("text/document+code")
}

@Service
class FileTypeService(private val configuration: Configuration) {

    suspend fun handleMessage(message: Message) {

        val metadataList = message.attachments.map { metadataOf(it) }

        metadataList.forEach { fileWrapper ->
            if (fileWrapper == null) return@forEach
            if (fileWrapper.fileMetadata.isAllowed) return@forEach

            val user = message.author!!.mention
            val type = fileWrapper.fileMetadata.typeAlias
            val channel = message.getChannel()
            val guild = message.getGuild()

            message.delete()

            println(fileWrapper.toString())

            when {
                type.startsWith("text") -> {
                    val sentMessage = channel.createMessage("that seems to be text, uploading to hasteb.in now... <a:loading:714196361888661594>")

                    sentMessage.edit {
                        content = FileUploader().uploadFile(fileWrapper)
                    }
                }
                else -> {
                    val guildConfiguration = configuration.getGuildConfig(guild.id.longValue) ?: return
                    channel.createMessage(responseFor(user, fileWrapper.fileMetadata.typeAlias))

                }
            }



        }
    }



    private fun metadataOf(attachment: Attachment): FileWrapper? {
        val contents = getFile(attachment.url) ?: return null
        val dataStream = contents.byteInputStream()
        val type = Tika().detect(dataStream)

        val metadata = FileMetadata(attachment.filename, type, commonAliasFor(type), isAllowed(type))
        val fileData = FileData(contents)

        return FileWrapper(metadata, fileData)
    }



    private fun isAllowed (type: String): Boolean {
        return type.startsWith("image") || type.startsWith("video") || type.startsWith("audio")
    }

    private fun commonAliasFor(type: String): String {
        return when (type) {
            "application/xml" -> CommonAlias.DOCUMENT.alias
            "application/json" -> CommonAlias.DOCUMENT.alias
            "application/ld+json" -> CommonAlias.DOCUMENT.alias
            "application/xhtml+xml" -> CommonAlias.DOCUMENT.alias
            else -> "those types of files"
        }
    }

    private fun responseFor(author: String, typeAlias: String): String {
        return "Please don't send $typeAlias here $author"
    }

    private fun getFile(url: String): String? {
        val (request, response, result) = url
                .httpGet()
                .responseString()

        return when (result) {
            is Result.Success -> {
                return result.get()
            }
            is Result.Failure -> {
                null
            }
        }
    }
}