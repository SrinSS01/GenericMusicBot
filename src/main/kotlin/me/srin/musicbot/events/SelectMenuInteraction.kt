package me.srin.musicbot.events

import me.srin.musicbot.lavaplayer.PlayerManager
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object SelectMenuInteraction: ListenerAdapter() {
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        when (event.componentId) {
            "playlist" -> {
                val description = event.selectedOptions[0].description!!
                PlayerManager.loadAndPlay(event.channel, description)
                event.deferEdit().queue()
            }
            else -> {}
        }
    }
}