package me.srin.musicbot.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager

class GuildMusicManager(manager: AudioPlayerManager) {
    val audioPlayer: AudioPlayer
    val scheduler: TrackScheduler
    val sendHandler: AudioPlayerSendHandler

    init {
        audioPlayer = manager.createPlayer()
        scheduler = TrackScheduler(player = audioPlayer)
        audioPlayer.addListener(scheduler)
        sendHandler = AudioPlayerSendHandler(audioPlayer)
    }
}