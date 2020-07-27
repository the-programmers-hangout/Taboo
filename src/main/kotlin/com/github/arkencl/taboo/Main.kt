package com.github.arkencl.taboo

import com.github.arkencl.taboo.data.loadConfig
import me.jakejmattson.kutils.api.dsl.bot
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
            }



            logging {
                generateCommandDocs = false
            }
        }
    }
}