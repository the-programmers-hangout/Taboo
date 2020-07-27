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
        val type = attachmentWrapper.fileMetadata.type
        return when {
            type.startsWith("text") || attachmentWrapper.fileMetadata.typeAlias == "xml" ->
                uploadToHastebin(attachmentWrapper.fileData.content)
            else -> "uh oh error go poopie"
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
                    return "we did it: https://hasteb.in/${it.key}"
                },

                failure = {
                    return "uh oh error go poopie: ${it.localizedMessage}"
                })
    }
}

