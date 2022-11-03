package me.srin.musicbot.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

object PlayerManager {
    private val musicManager = HashMap<Long, GuildMusicManager>()
    private val audioPlayerManager = DefaultAudioPlayerManager()

    init {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager)
        AudioSourceManagers.registerLocalSource(audioPlayerManager)
    }

    operator fun get(guild: Guild) = musicManager.computeIfAbsent(guild.idLong) {
        val guildMusicManager = GuildMusicManager(audioPlayerManager)
        guild.audioManager.sendingHandler = guildMusicManager.sendHandler
        guildMusicManager
    }

    fun loadAndPlay(channel: MessageChannelUnion, trackUrl: String) {
        val guildMusicManager = this[channel.asGuildMessageChannel().guild]
        audioPlayerManager.loadItemOrdered(guildMusicManager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(audioTrack: AudioTrack) {
                guildMusicManager.scheduler.queue(audioTrack)
                val info = audioTrack.info
                channel.sendMessage("Added to queue: `${info.title}` by `${info.author}`").queue()
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                val tracks = playlist.tracks
                if (trackUrl.isYTSearch()) {
                    val menu = StringSelectMenu.create("playlist")
                        .setPlaceholder("Select an url")
                        .setRequiredRange(1, 1)
                    for (track in tracks) {
                        val info = track.info
                        menu.addOption(info.author, info.title, info.uri)
                    }
                    channel.sendMessageComponents(ActionRow.of(menu.build())).queue()
                    return
                }
                channel.sendMessage("Added to queue: `${tracks.size}` tracks from playlist `${playlist.name}`").queue()
                tracks.forEach(guildMusicManager.scheduler::queue)
            }

            override fun noMatches() {
            }

            override fun loadFailed(p0: FriendlyException?) {
            }
        })
    }
}

private fun String.isYTSearch(): Boolean = startsWith(prefix = "ytsearch:")