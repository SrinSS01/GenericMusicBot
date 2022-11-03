package me.srin.musicbot

import lombok.Getter
import lombok.Setter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties
@ConfigurationProperties("bot")
@Getter @Setter
class Config {
    lateinit var token: String
}