package com.github.arkencl.taboo.service

import com.github.arkencl.taboo.listener.AttachmentWrapper
import com.github.arkencl.taboo.listener.FileMetadata
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import me.jakejmattson.kutils.api.annotations.Service

private data class HasteResponse(val key: String = "")

@Service
class FileUploader(){

    fun uploadFile(attachmentWrapper: AttachmentWrapper): String {
        val type = attachmentWrapper.fileMetadata.typeAlias
        return when {
            type.startsWith("text") ->
                uploadToHastebin(attachmentWrapper.fileData.content)
            else -> "An unknown error has occurred."
        }
    }

    private fun uploadToHastebin(fileContent: String): String {
        val bodyJson: String = fileContent.trimIndent()

        val (request, response, result) = Fuel
                .post("https://hasteb.in/documents")
                .set("User-Agent", "The Programmers Hangout (https://github.com/the-programmers-hangout/)")
                .body(bodyJson)
                .responseObject<HasteResponse>()

        result.fold(
                success = {
                    return "File uploaded to hasteb.in: https://hasteb.in/${it.key}"
                },

                failure = {
                    return "Unable to upload file to hasteb.in: ${it.localizedMessage}"
                })
    }
}

