package org.teknux.webapp

import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.dataloader.DataLoaderRegistry
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.teknux.webapp.graphql.resolver.Mutation
import org.teknux.webapp.graphql.resolver.Query
import org.teknux.webapp.graphql.resolver.UserResolver
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.User
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
    class CustomizationBean : WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        override fun customize(container: ConfigurableServletWebServerFactory) {
            container.setPort((System.getProperty("port") ?: "8080").toInt())
        }
    }

    @Bean
    fun init(storeService: StoreService) = CommandLineRunner {
        LOGGER.info("### Init Data ..")
        for (n in 1..10) {
            var user = storeService.newUser(User(name = "user_$n"))
            LOGGER.info("${user.name} created : ${user.id}")
            user.id?.let {
                storeService.addAction(ClockAction(type = 1, desc = "clockin", userId = it))
                LOGGER.info("${user.name} clocked-in")
                storeService.addAction(ClockAction(type = 0, desc = "clockout", userId = it))
                LOGGER.info("${user.name} clocked-out")
            }
        }
        LOGGER.info("### Done Init Data")
    }

    @Bean
    fun dataLoaderRegistry(loaderList: List<DataLoader<*, *>>): DataLoaderRegistry {
        val registry = DataLoaderRegistry()
        for (loader in loaderList) {
            registry.register(loader.javaClass.simpleName, loader)
        }
        return registry
    }

    @Bean
    fun instrumentation(dataLoaderRegistry: DataLoaderRegistry): Instrumentation {
        return DataLoaderDispatcherInstrumentation(dataLoaderRegistry)
    }

    /*
    /**
     * Initialize the DataLoader for User
     *
     * @param storeService the service used to access data
     */
    @Bean
    fun initUserToClockActionsDataLoader(storeService: StoreService): UserResolver.UserToClockActionsDataLoader {
        return UserResolver.UserToClockActionsDataLoader(
                fetcher = { storeService.getActions(it).orEmpty() },
                keySelector = { it.userId },
                options = DataLoaderOptions.newOptions().setCachingEnabled(false).setBatchingEnabled(true))
    }
    */

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
