package org.teknux.webapp.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.teknux.webapp.graphql.dataloader.ClockActionsToOffice
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.service.StoreService
import java.util.concurrent.CompletableFuture

@Component
class ClockActionResolver(private val storeService: StoreService): GraphQLResolver<ClockAction> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ClockActionResolver::class.java)
    }

    @Autowired
    private lateinit var clockActionsToOffice: ClockActionsToOffice

    fun getOffice(clockAction: ClockAction): CompletableFuture<Office> {
        LOGGER.debug("[GraphQL Resolver] getOffice(clockAction=[$clockAction])")
        return clockActionsToOffice.load(clockAction.id)
    }
}