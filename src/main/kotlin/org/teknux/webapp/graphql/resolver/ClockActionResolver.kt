package org.teknux.webapp.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.teknux.webapp.graphql.dataloader.OfficeIdsToOfficesDataLoader
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import java.util.concurrent.CompletableFuture

@Component
class ClockActionResolver: GraphQLResolver<ClockAction> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ClockActionResolver::class.java)
    }

    @Autowired
    private lateinit var officeIdsToOffices: OfficeIdsToOfficesDataLoader

    fun getOffice(clockAction: ClockAction): CompletableFuture<Office> {
        LOGGER.debug("[GraphQL Resolver] getOffice(clockAction=[$clockAction])")
        return officeIdsToOffices.load(clockAction.office!!.id)
    }
}