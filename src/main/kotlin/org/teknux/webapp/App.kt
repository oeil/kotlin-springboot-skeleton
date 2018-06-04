package org.teknux.webapp

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.service.StoreService


@SpringBootApplication
class App() {

    private var context: ConfigurableApplicationContext? = null

    fun start(args: Array<String>) {
        context = runApplication<App>(*args)
    }

    fun stop() {
        val exitCode = SpringApplication.exit(context, ExitCodeGenerator { 0 })
        System.exit(exitCode)
    }

    @Component
    class CustomizationBean : WebServerFactoryCustomizer<ConfigurableReactiveWebServerFactory> {
        override fun customize(server: ConfigurableReactiveWebServerFactory) {
            server.setPort((System.getProperty("port") ?: "8080").toInt())
        }
    }

    @Bean
    fun init(storeService: StoreService) = CommandLineRunner {
        LOGGER.info("### Init Data ..")
        for (n in 1..10) {
            var user = storeService.newUser("user_$n")
            LOGGER.info("${user.name} created : ${user.id}")
            storeService.addAction(ClockAction(type = 1, desc = "clockin", user = user))
            LOGGER.info("${user.name} clocked-in")
            storeService.addAction(ClockAction(type = 0, desc = "clockout", user = user))
            LOGGER.info("${user.name} clocked-out")
        }
        LOGGER.info("### Done Init Data")
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
