package me.moeszyslak.taboo.services

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.FileWrapper

data class HasteResponse(val key: String = "")

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

    fun uploadToHastebin(fileContent: String): String {
        val bodyJson: String = fileContent.trimIndent()

        val (request, response, result) = Fuel
                .post("https://hasteb.in/documents")
                .set("User-Agent", "The Programmers Hangout (https://github.com/the-programmers-hangout/)")
                .body(bodyJson)
                .responseObject<HasteResponse>()

        result.fold<Nothing>(
                success = {
                    return "File uploaded to hasteb.in: https://hasteb.in/${it.key}"
                },

                failure = {
                    return "Unable to upload file to hasteb.in: ${it.localizedMessage}"
                })
    }
}