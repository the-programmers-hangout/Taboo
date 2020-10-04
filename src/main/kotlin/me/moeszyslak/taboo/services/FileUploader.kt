package me.moeszyslak.taboo.services

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.FileWrapper

data class PasteResponse(val key: String = "")

@Service
class FileUploader{

    fun uploadFile(fileWrapper: FileWrapper): String {
        val type = fileWrapper.fileMetadata.type
        return when {
            type.startsWith("text") ->
                uploadToHastebin(fileWrapper.fileData.content)
            else -> "An unknown error has occurred."
        }
    }

    private fun uploadToHastebin(fileContent: String): String {
        val bodyJson: String = fileContent.trimIndent()

        val (_, _, result) = Fuel
                .post("https://pastecord.com/documents")
                .set("User-Agent", "The Programmers Hangout (https://github.com/the-programmers-hangout/)")
                .body(bodyJson)
                .responseObject<PasteResponse>()

        result.fold<Nothing>(
                success = {
                    return "File uploaded to pastecord: https://pastecord.com/${it.key}"
                },

                failure = {
                    return "Unable to upload file to pastecord: ${it.localizedMessage}"
                })
    }
}