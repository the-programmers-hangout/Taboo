package me.moeszyslak.taboo.extensions

import dev.kord.common.entity.Snowflake

fun Snowflake.long() = this.asString.toLong()