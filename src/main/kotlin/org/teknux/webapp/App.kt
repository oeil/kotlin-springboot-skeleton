package org.teknux.webapp

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.stereotype.Component


@SpringBootApplication
open class App() {

    private var context: ConfigurableApplicationContext? = null

    fun start(args: Array<String>) {
        context = runApplication<App>(*args)
    }

    fun stop() {
        val exitCode = SpringApplication.exit(context, ExitCodeGenerator { 0 })
        System.exit(exitCode)
    }

    @Component
    class CustomizationBean : WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        override fun customize(container: ConfigurableServletWebServerFactory) {
            container.setPort((System.getProperty("port") ?: "8080").toInt())
        }
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(App::class.java)

        private var app: App? = null

        @JvmStatic
        fun main(args: Array<String>) {
            LOGGER.info("Starting up App...")
            app = App().apply { start(args) }
        }

        fun stop() {
            app?.stop()
        }
    }
}