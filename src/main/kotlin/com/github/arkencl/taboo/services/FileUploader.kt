package com.github.arkencl.taboo.services

import com.github.arkencl.taboo.dataclasses.FileWrapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import me.jakejmattson.kutils.api.annotations.Service

private data class HasteResponse(val key: String = "")

@Service
class FileUploader(){

    fun uploadFile(fileWrapper: FileWrapper): String {
        val type = fileWrapper.fileMetadata.typeAlias
        return when {
            type.startsWith("text") ->
                uploadToHastebin(fileWrapper.fileData.content)
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

