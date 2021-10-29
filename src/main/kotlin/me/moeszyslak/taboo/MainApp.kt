package me.moeszyslak.taboo

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.kord.x.emoji.Emojis
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.pfpUrl
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.data.Permissions
import me.moeszyslak.taboo.extensions.long
import me.moeszyslak.taboo.services.StatisticsService
import java.awt.Color
import kotlin.time.ExperimentalTime

@PrivilegedIntent
@KordPreview
@ExperimentalTime
suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null
    val prefix = System.getenv("DEFAULT_PREFIX") ?: "<none>"
    require(token != null) { "Expected the bot token as an environment variable" }

    bot(token) {

        val configuration = data("config/config.json") { Configuration() }

        prefix {
            guild?.let { configuration[it.id.long()]?.prefix } ?: prefix
        }

        configure {
            allowMentionPrefix = true
            generateCommandDocs = true
            showStartupLog = true
            commandReaction = Emojis.eyes
            theme = Color(0x00BFFF)
            entitySupplyStrategy = EntitySupplyStrategy.cacheWithRestFallback
            intents = Intents.all
            permissions(Permissions.STAFF)
        }


        mentionEmbed {
            val statsService = it.discord.getInjectionObjects(StatisticsService::class)
            val guildConfiguration = configuration[it.guild!!.id.long()]

            title = "Taboo"
            description = "A file listener discord bot to prevent those pesky files from being shared"

            color = it.discord.configuration.theme!!

            thumbnail {
                url = it.discord.kord.getSelf().pfpUrl
            }

            field {
                name = "Prefix"
                value = it.prefix()
                inline = true
            }

            field {
                name = "Ping"
                value = statsService.ping
                inline = true
            }

            if (guildConfiguration != null) {
                val staffRole = it.guild!!.getRole(Snowflake(guildConfiguration.staffRole))
                val loggingChannel = it.guild!!.getChannel(Snowflake(guildConfiguration.logChannel))
                field {

                    name = "Configuration"
                    value = "```" +
                            "Staff Role: ${staffRole.name}\n" +
                            "Logging Channel: ${loggingChannel.name}\n" +
                            "```"
                }
            }


            field {
                val versions = it.discord.versions

                name = "Bot Info"
                value = "```" +
                        "Version: 2.1.2\n" +
                        "DiscordKt: ${versions.library}\n" +
                        "Kord: ${versions.kord}\n" +
                        "Kotlin: ${versions.kotlin}" +
                        "```"
            }

            field {
                name = "Uptime"
                value = statsService.uptime
                inline = true
            }

            field {
                name = "Source"
                value = "[GitHub](https://github.com/the-programmers-hangout/taboo)"
                inline = true
            }
        }


    }
}