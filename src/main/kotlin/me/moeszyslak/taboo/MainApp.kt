package me.moeszyslak.taboo

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.gateway.Intent
import com.gitlab.kordlib.gateway.Intents
import com.gitlab.kordlib.gateway.PrivilegedIntent
import com.gitlab.kordlib.kordx.emoji.Emojis
import me.jakejmattson.discordkt.api.dsl.bot
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.PermissionsService
import me.moeszyslak.taboo.services.StatisticsService
import java.awt.Color
import kotlin.time.ExperimentalTime

@PrivilegedIntent
@ExperimentalTime
suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null
    val prefix = System.getenv("DEFAULT_PREFIX") ?: "<none>"
    require(token != null) { "Expected the bot token as an environment variable" }

    bot(token) {

        prefix {
            val configuration = discord.getInjectionObjects(Configuration::class)
            guild?.let { configuration.get(it.id)?.prefix } ?: prefix
        }

        configure {
            allowMentionPrefix = true
            generateCommandDocs = true
            showStartupLog = true
            commandReaction = Emojis.eyes
            theme = Color(0x00BFFF)
        }

        mentionEmbed {
            val configuration = it.discord.getInjectionObjects(Configuration::class)
            val statsService = it.discord.getInjectionObjects(StatisticsService::class)
            val guildConfiguration = configuration[it.guild!!.id]

            title = "Taboo"
            description = "A file listener discord bot to prevent those pesky files from being shared"

            color = it.discord.configuration.theme

            thumbnail {
                url = it.discord.api.getSelf().avatar.url
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
                val staffRole = it.guild!!.getRole(guildConfiguration.staffRole)
                val loggingChannel = it.guild!!.getChannel(guildConfiguration.logChannel)
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
                        "Version: 1.0.0\n" +
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

        permissions {
            val requiredPermissionLevel = command.requiredPermissionLevel
            val guild = guild ?: return@permissions false
            val member = user.asMember(guild.id)

            val permissionsService = discord.getInjectionObjects(PermissionsService::class)
            return@permissions permissionsService.hasClearance(member, requiredPermissionLevel)
        }

        intents {
            Intents.nonPrivileged.intents.forEach {
                +it
            }

            +Intent.GuildMembers
        }
    }
}