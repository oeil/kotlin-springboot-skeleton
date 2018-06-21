package org.teknux.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import org.dataloader.DataLoader
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
import org.teknux.webapp.model.DataGenerator
import org.teknux.webapp.service.IStoreService
import org.springframework.web.context.WebApplicationContext
import javax.servlet.ServletException
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.web.context.annotation.RequestScope
import org.teknux.webapp.graphql.dataloader.UsersToClockActionsDataLoader


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
    fun init(storeService: IStoreService) = CommandLineRunner {
        val genOfficeCount = (System.getProperty("genOffices") ?: "100").toInt()
        val genUserCount = (System.getProperty("genUsers") ?: "100").toInt()
        val genClockActionsPerUserCount = (System.getProperty("genActions") ?: "10").toInt()
        DataGenerator(storeService).generate(genOfficeCount, genUserCount, genClockActionsPerUserCount)
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
