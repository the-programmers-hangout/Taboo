package me.moeszyslak.taboo.services

import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.utilities.timeToString
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Service
class StatisticsService(private val configuration: Configuration, private val discord: Discord) {
    private var startTime: Date = Date()

    val uptime: String
        get() = timeToString(Date().time - startTime.time)


    val ping: String
        get() = "${discord.kord.gateway.averagePing!!.toDouble(DurationUnit.MILLISECONDS).toInt()} ms"

}