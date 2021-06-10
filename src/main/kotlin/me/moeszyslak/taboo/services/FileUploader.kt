package me.moeszyslak.taboo.services

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.FileWrapper

data class PasteResponse(val key: String = "")

@Service
class FileUploader {

    fun upload(file: FileWrapper): Result<String, Exception> {
        return upload(file.fileData.content)
    }

    fun upload(content: String): Result<String, Exception> {
        val (_, _, result) = Fuel
            .post("https://pastecord.com/documents")
            .set("User-Agent", "The Programmers Hangout (https://github.com/the-programmers-hangout/)")
            .body(content.trimIndent())
            .responseObject<PasteResponse>()

        return result.map { "https://pastecord.com/${it.key}" }
    }
}
