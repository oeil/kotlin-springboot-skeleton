package org.teknux.webapp

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class App() {

    companion object {

        private val LOGGER = LoggerFactory.getLogger(App::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            LOGGER.info("Starting up App...")
            runApplication<App>(*args)
        }
    }
}