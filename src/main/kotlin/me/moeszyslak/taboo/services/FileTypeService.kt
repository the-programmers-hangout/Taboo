package me.moeszyslak.taboo.services

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.entity.Attachment
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Message
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.data.FileData
import me.moeszyslak.taboo.data.FileMetadata
import me.moeszyslak.taboo.data.FileWrapper
import org.apache.tika.Tika
import org.apache.tika.config.TikaConfig

@Service
class FileTypeService(private val configuration: Configuration, private val loggerService: LoggerService) {

    suspend fun handleMessage(message: Message) {

        val metadataList = message.attachments.map { metadataOf(it, message.getGuild()) }

        metadataList.forEach { fileWrapper ->
            if (fileWrapper == null) return@forEach
            if (fileWrapper.fileMetadata.isAllowed) return@forEach

            val user = message.author!!.mention
            val member = message.getAuthorAsMember()!!
            val type = fileWrapper.fileMetadata.type
            val channel = message.getChannel()
            val guild = message.getGuild()

            message.delete()

            val config = configuration[guild.id.longValue] ?: return@forEach
            val shouldUpload = config.mimeRules[type]?.uploadText ?: false
            when {
                shouldUpload -> {
                    loggerService.logUploaded(guild, member, channel, fileWrapper)
                    val sentMessage = channel.createMessage("uploading to pastecord...")

                    sentMessage.edit {
                        content = FileUploader().uploadFile(fileWrapper)
                    }
                }
                else -> {
                    loggerService.logDeleted(guild, member, channel, fileWrapper)

                    val response = customResponse(type, guild)
                            ?: responseFor(user, fileWrapper.fileMetadata.typeAlias)

                    channel.createMessage(response)
                }
            }
        }

    }


    private fun metadataOf(attachment: Attachment, guild: Guild): FileWrapper? {
        val contents = getFile(attachment.url) ?: return null
        val dataStream = contents.byteInputStream()
        val type = Tika().detect(dataStream)

        val metadata = FileMetadata(attachment.filename, type, commonAliasFor(type), isAllowed(type, guild))
        val fileData = FileData(contents)

        return FileWrapper(metadata, fileData)
    }


    private fun isAllowed(type: String, guild: Guild): Boolean {
        val config = configuration[guild.id.longValue] ?: return false

        return config.ignoredMimes.contains(type)
    }

    private fun commonAliasFor(type: String): String {

        val mimeType = TikaConfig().mimeRepository.forName(type)

        if (mimeType.acronym.isBlank())
            return "those type of files"

        return mimeType.acronym + " files"
    }

    private fun customResponse(mime: String, guild: Guild): String? {
        val config = configuration[guild.id.longValue] ?: return null
        val mimeConfig = config.mimeRules[mime] ?: return null

        return mimeConfig.message
    }

    private fun responseFor(author: String, typeAlias: String): String {
        return "Please don't send $typeAlias here $author"
    }

    private fun getFile(url: String): String? {
        val (_, _, result) = url
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