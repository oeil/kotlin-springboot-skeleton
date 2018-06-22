package org.teknux.webapp

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
import org.teknux.webapp.model.GenData
import org.teknux.webapp.service.IStoreService


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
            container.setPort((System.getProperty("port") ?: "8282").toInt())
        }
    }

    /**
     * Generate initial random  data set when requested via the startup VM option. Provide following command option to start the app:
     * <P>
     *     -DgenData=offices:COUNT|users:COUNT|actions:COUNT
     * </P>
     */
    @Bean
    fun genInitialData(storeService: IStoreService) = CommandLineRunner {
        System.getProperty("genData")?.let {
            val genData = GenData()
            it.split("|").stream().forEach {
                val key = it.split(":")[0]
                val value = it.split(":")[1].toInt()
                when(key) {
                    "offices" -> genData.offices = value
                    "users" -> genData.users = value
                    "actions" -> genData.actions = value
                }
            }
            DataGenerator(storeService).generate(genData.offices, genData.users, genData.actions)
        }
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
