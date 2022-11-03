package me.srin.musicbot

import me.srin.musicbot.commands.Command
import me.srin.musicbot.events.OnGuildReady
import me.srin.musicbot.events.SelectMenuInteraction
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class Main(val config: Config): CommandLineRunner {
    override fun run(vararg args: String?) {
        JDABuilder.createDefault(config.token)
            .enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES
            )
            .disableCache(
                CacheFlag.CLIENT_STATUS,
                CacheFlag.ACTIVITY,
                CacheFlag.EMOJI
            )
            .enableCache(CacheFlag.VOICE_STATE)
            .addEventListeners(Command, OnGuildReady, SelectMenuInteraction)
            .build().run {
                Utils.EXECUTER.scheduleWithFixedDelay({
                      if (readLine() == "stop") {
                          Utils.EXECUTER.shutdownNow()
                          shutdownNow()
                      }
                }, 0, 1, TimeUnit.SECONDS)
            }
    }
}