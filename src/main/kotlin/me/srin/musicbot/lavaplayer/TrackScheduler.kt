package me.srin.musicbot.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class TrackScheduler(val player: AudioPlayer): AudioEventAdapter() {
    private val queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if (endReason?.mayStartNext == true) {
            nextTrack()
        }
    }

    fun clear() {
        queue.clear()
    }

    fun queue(audioTrack: AudioTrack) {
        if (!player.startTrack(audioTrack, true)) {
            queue.offer(audioTrack)
        }
    }

    fun nextTrack() {
        player.startTrack(queue.poll(), false)
    }
}