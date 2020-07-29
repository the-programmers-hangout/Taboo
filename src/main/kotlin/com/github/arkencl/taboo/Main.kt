package com.github.arkencl.taboo

import com.github.arkencl.taboo.dataclass.loadConfig
import com.github.arkencl.taboo.locale.Links
import com.github.arkencl.taboo.locale.Project
import me.jakejmattson.kutils.api.dsl.bot
import me.jakejmattson.kutils.api.extensions.jda.fullName
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import java.awt.Color
import java.util.*

fun main(){
    loadConfig {
        val configuration = it ?: throw Exception("Failed to parse configuration")

        bot(configuration.token) {
            client { token ->
                JDABuilder.createDefault(token)
                        .setChunkingFilter(ChunkingFilter.ALL)
                        .enableIntents(EnumSet.allOf(GatewayIntent::class.java))
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
            }

            injection {
                inject(configuration)
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


                mentionEmbed {
                    val discord = it.discord
                    val properties = discord.properties
                    val self = discord.jda.selfUser

                    author {
                        discord.jda.retrieveUserById(412540774694256640).queue {
                            iconUrl = it.effectiveAvatarUrl
                            name = it.fullName()
                            url = Links.DISCORD_ACCOUNT
                        }
                    }

                    simpleTitle = "${self.fullName()} (Taboo 0.1.0)"
                    description = Project.BOT
                    thumbnail = self.effectiveAvatarUrl
                    color = infoColor

                    addInlineField("Prefix", configuration.prefix)
                    addInlineField("Contributors", Project.CONTRIBUTORS)
                    addField("Build Info", "```\n" +
                            "KUtils Version: ${properties.kutilsVersion}\n" +
                            "JDA Version: ${properties.jdaVersion}\n" +
                            "```")
                    addField("Source", Project.REPO)
                }
            }


            logging {
                generateCommandDocs = false
            }
        }
    }
}