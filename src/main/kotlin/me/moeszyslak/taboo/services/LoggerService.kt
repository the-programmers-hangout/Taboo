package me.moeszyslak.taboo.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.getChannelOfOrNull
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.data.FileWrapper
import me.moeszyslak.taboo.extensions.descriptor

@Service
class LoggerService(private val configuration: Configuration) {

    private suspend fun withLog(guild: Guild, f: () -> String) = getLogConfig(guild.id).apply {
        log(guild, getLogConfig(guild.id), f())
    }


    suspend fun logDeleted(guild: Guild, member: Member, channel: MessageChannel, fileWrapper: FileWrapper) = withLog(guild) {
        "Deleted ${fileWrapper.fileMetadata.name} " +
            "with a MIME of ${fileWrapper.fileMetadata.type} " +
            "in ${channel.mention} " +
            "from ${member.descriptor()}"
    }

    suspend fun logUploaded(guild: Guild, member: Member, channel: MessageChannel, fileWrapper: FileWrapper) = withLog(guild) {
        "Deleted and uploaded ${fileWrapper.fileMetadata.name} " +
            "with a MIME of ${fileWrapper.fileMetadata.type} " +
            "in ${channel.mention} " +
            "from ${member.descriptor()}"
    }


    private fun getLogConfig(guildId: Snowflake) = configuration.guildConfigurations[guildId]!!.logChannel

    private suspend fun log(guild: Guild, logChannelId: Snowflake, message: String) =
        guild.getChannelOfOrNull<TextChannel>(logChannelId)?.createMessage(message)
}