package com.github.arkencl.taboo

import com.github.arkencl.taboo.dataclasses.loadConfig
import com.github.arkencl.taboo.listeners.FileListener
import com.github.arkencl.taboo.locale.Link
import com.github.arkencl.taboo.locale.ProjectDescription
import com.github.arkencl.taboo.services.PermissionsService
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.jda.fullName
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import java.awt.Color
import java.util.*


fun main(){
    loadConfig {
        val config = it ?: throw Exception("Failed to parse configuration")
        bot(config.token) {
            client { token ->
                JDABuilder.createDefault(token)
                        .setChunkingFilter(ChunkingFilter.ALL)
                        .enableIntents(EnumSet.allOf(GatewayIntent::class.java))
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
            }

            configure {
                colors {
                    infoColor = Color.CYAN
                    failureColor = Color.RED
                    successColor = Color.GREEN
                }

                commandReaction = null
                allowMentionPrefix = true
                requiresGuild = true

                prefix {
                    config.prefix
                }

                mentionEmbed {
                    val discord = it.discord
                    val properties = discord.properties
                    val self = discord.jda.selfUser

                    author {
                        discord.jda.retrieveUserById(412540774694256640).queue {
                            iconUrl = it.effectiveAvatarUrl
                            name = it.fullName()
                            url = Link.DISCORD_ACCOUNT
                        }
                    }

                    simpleTitle = "${self.fullName()} (Taboo 0.1.0)"
                    description = ProjectDescription.BOT
                    thumbnail = self.effectiveAvatarUrl
                    color = infoColor

                    addInlineField("Prefix", config.prefix)
                    addInlineField("Contributors", ProjectDescription.CONTRIBUTORS)
                    addField("Build Info", "```\n" +
                            "DiscordKt Version: ${properties.libraryVersion}\n" +
                            "JDA Version: ${properties.jdaVersion}\n" +
                            "```")
                    addField("Source", ProjectDescription.REPO)
                }
            }

            injection {
                inject(config)
            }

            logging {
                generateCommandDocs = false
            }
        }
    }
}
