package me.moeszyslak.taboo

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.kordx.emoji.Emojis
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.*
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.extensions.requiredPermissionLevel
import me.moeszyslak.taboo.services.PermissionsService
import me.moeszyslak.taboo.services.StatisticsService
import java.awt.Color
import kotlin.time.ExperimentalTime

@ExperimentalTime
suspend fun main(args: Array<String>) {
    //Get the bot token from the command line (or your preferred way).
    val token = args.firstOrNull()
    require(token != null) { "Expected the bot token as a command line argument!" }

    //Start the bot and set configuration options.
    bot(token) {



        prefix {
            val configuration = discord.getInjectionObjects(Configuration::class)
            guild?.let { configuration[it.id.longValue]?.prefix } ?: "<none>"
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
            val configuration = it.discord.getInjectionObjects(Configuration::class)
            val statsService = it.discord.getInjectionObjects(StatisticsService::class)
            val guildConfiguration = configuration[it.guild!!.id.longValue]

            val staffRole = it.guild!!.getRole(Snowflake(guildConfiguration!!.staffRole))
            val loggingChannel = it.guild!!.getChannel(Snowflake(guildConfiguration!!.logChannel))

            title = "Taboo"
            description = "A file listener discord bot to prevent those pesky files from being shared"

            color = it.discord.configuration.theme

            thumbnail {
                url = api.getSelf().avatar.url
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

            field {

                name = "Configuration"
                value = "```" +
                        "Staff Role: ${staffRole.name}\n" +
                        "Logging Channel: ${loggingChannel.name}\n" +
                        "```"
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