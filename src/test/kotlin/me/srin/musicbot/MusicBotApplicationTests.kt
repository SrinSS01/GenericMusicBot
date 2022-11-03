package me.srin.musicbot

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MusicBotApplicationTests {
    @Test
    fun regexTest() {
        val str = "hi-mom"
        val new = str.replace(Regex("(\\w+)-(\\w+)")) { matchResult ->
            val groups = matchResult.groups
            val group2 = groups[2]?.value
            "${groups[1]?.value}${group2?.get(0)?.uppercase()}${group2?.substring(1)}"
        }
        println(new)
        assert(new == "hiMom")
    }

    @Test
    fun contextLoads() {
    }

}
