package me.srin.musicbot.events

import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

object OnGuildReady: ListenerAdapter() {
    override fun onGuildReady(event: GuildReadyEvent) {
        event.guild.run {
            updateCommands().addCommands(
                Commands.slash("join", "join a voice channel"),
                Commands.slash("skip", "skip the currently playing music"),
                Commands.slash("clear", "clear the queue"),
                Commands.slash("play", "start playing music").addOption(OptionType.STRING, "url", "song url",true),
            ).queue()
        }
    }
}