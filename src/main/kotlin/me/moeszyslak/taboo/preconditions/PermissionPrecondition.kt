//package me.moeszyslak.taboo.preconditions
//
//import me.jakejmattson.discordkt.api.dsl.*
//import me.moeszyslak.taboo.extensions.requiredPermissionLevel
//import me.moeszyslak.taboo.services.Permission
//import me.moeszyslak.taboo.services.PermissionsService
//
//
//class PermissionPrecondition(private val permissionsService: PermissionsService) : Precondition() {
//    override suspend fun evaluate(event: CommandEvent<*>): PreconditionResult {
//        val command = event.command ?: return Fail()
//        val requiredPermissionLevel = command.requiredPermissionLevel
//        val guild = event.guild!!
//        val member = event.author.asMember(guild.id)
//
//        val response = when (requiredPermissionLevel) {
//            Permission.BOT_OWNER -> "Missing clearance to use this command. You must be the bot owner."
//            Permission.GUILD_OWNER -> "Missing clearance to use this command. You must be the guild owner."
//            else -> ""
//        }
//
//        if (!permissionsService.hasClearance(member, requiredPermissionLevel))
//            return Fail(response)
//
//        return Pass
//    }
//}