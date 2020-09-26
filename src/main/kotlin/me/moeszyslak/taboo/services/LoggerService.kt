package me.moeszyslak.taboo.services

import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.utilities.timeToString
import java.util.*

@Service
class LoggerService(private val configuration: Configuration, private val discord: Discord) {

}