@file:Suppress("unused")

package me.srin.musicbot.commands

import me.srin.musicbot.lavaplayer.PlayerManager
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory

object Command: ListenerAdapter() {
    private val logger = LoggerFactory.getLogger(Command::class.java)
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.guild == null) {
            return
        }
        val replace = event.name.replace(Regex("(\\w+)-(\\w+)")) { matchResult ->
            val groups = matchResult.groups
            val group2 = groups[2]?.value
            "${groups[1]?.value}${group2?.get(0)?.uppercase()}${group2?.substring(1)}"
        }
        Command::class.java.getDeclaredMethod(replace, event.javaClass).invoke(null, event)
    }

    @JvmStatic
    fun join(event: SlashCommandInteractionEvent) {
        val guild = event.guild!!
        val selfMember = guild.selfMember
        val botVoiceState = selfMember.voiceState!!
        val member = event.member!!
        val memberVoiceState = member.voiceState!!

        if (!botVoiceState.inAudioChannel()) {
            if (!memberVoiceState.inAudioChannel()) {
                event.reply("You need to be in a voice channel to use this command").setEphemeral(true).queue()
                return
            }
            val audioManager = guild.audioManager
            val memberVoiceChannel = memberVoiceState.channel
            val audioChannel = memberVoiceChannel!!
            audioManager.openAudioConnection(audioChannel)
            event.reply("Connecting to `\uD83D\uDD08 ${ audioChannel.name }`").queue()
        } else event.reply("I'm already in an audio channel").setEphemeral(true).queue()
    }

    @JvmStatic
    fun play(event: SlashCommandInteractionEvent) {
        if (!event.isValidateState()) return
        var url = event.getOption("url")!!.asString
        if (url.isNotValidUrl()) {
            url = "ytsearch:$url"
        }
        event.reply("Searching...").mentionRepliedUser(false).queue()
        PlayerManager.loadAndPlay(channel = event.channel, trackUrl = url)
    }

    @JvmStatic
    fun clear(event: SlashCommandInteractionEvent) {
        if (!event.isValidateState()) return
        val scheduler = PlayerManager[event.guild!!].scheduler
        scheduler.player.stopTrack()
        scheduler.clear()

        event.reply("Stopped the music and cleared the queue").queue()
    }

    @JvmStatic
    fun skip(event: SlashCommandInteractionEvent) {
        if (!event.isValidateState()) return
        val guildMusicManager = PlayerManager[event.guild!!]
        if (guildMusicManager.audioPlayer.playingTrack == null) {
            event.reply("There is no track playing currently").setEphemeral(true).queue()
            return
        }
        guildMusicManager.scheduler.nextTrack()
        event.reply("Skipped").queue()
    }
}

private fun SlashCommandInteractionEvent.isValidateState(): Boolean {
    val guild = guild!!
    val selfMember = guild.selfMember
    val botVoiceState = selfMember.voiceState!!
    val member = member!!
    val memberVoiceState = member.voiceState!!

    if (!botVoiceState.inAudioChannel()) {
        reply("I'm not in a voice channel").setEphemeral(true).queue()
        return false
    }

    if (!memberVoiceState.inAudioChannel()) {
        reply("You need to be in a voice channel to use this command").setEphemeral(true).queue()
        return false
    }

    val memberVoiceChannel = memberVoiceState.channel
    if (memberVoiceChannel!!.idLong != botVoiceState.channel!!.idLong) {
        reply("You need to be in a same voice channel as me for this command to work").setEphemeral(true).queue()
        return false
    }
    return true
}

private fun String.isNotValidUrl(): Boolean =
    !matches(Regex("https://www\\.youtube\\.com/(?:(watch\\?v=[\\w-]+)|(playlist\\?list=[\\w-]+))"))

