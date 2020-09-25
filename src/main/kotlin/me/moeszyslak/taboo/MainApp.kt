package me.moeszyslak.taboo

import com.gitlab.kordlib.kordx.emoji.Emojis
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.*
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.PermissionsService
import java.awt.Color

suspend fun main(args: Array<String>) {
    val token = args.firstOrNull()
    require(token != null) { "Expected the bot token as a command line argument!" }

    bot(token) {
        prefix {
            "+"
        }

        configure {
            allowMentionPrefix = true
            generateCommandDocs = true
            showStartupLog = true
            requiresGuild = true
            commandReaction = Emojis.eyes
            theme = Color(0x00BFFF)
        }

        mentionEmbed {
           title = "Hello World"
        color = it.discord.configuration.theme

            author {
                with(it.author) {
                    icon = avatar.url
                    name = tag
                    url = profileLink
                }
            }

            thumbnail {
                url = api.getSelf().avatar.url
            }

            footer {
                val versions = it.discord.versions
                text = "${versions.library} - ${versions.kord} - ${versions.kotlin}"
            }

            addField("Prefix", it.prefix())
        }

        //Determine if the given command can be run with these conditions.
        permissions {
            val guild = guild ?: return@permissions false
            val member = user.asMember(guild.id)
            val permission = command.requiredPermissionLevel

            val permissionsService = discord.getInjectionObjects(PermissionsService::class)

            permissionsService.hasClearance(member, permission)
        }
    }
}