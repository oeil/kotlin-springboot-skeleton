package org.teknux.webapp.graphsql.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.slf4j.LoggerFactory
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService

class Query(private val storeService: StoreService) : GraphQLQueryResolver {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Query::class.java)
    }

    fun users(id: Int?): Iterable<User> {
        LOGGER.debug("[GraphQL QUERY] users(id=$id)")

        id?.let {
            return setOf(storeService.getUser(id))
        } ?: return storeService.getUsers()
    }

    fun clockActions(userId: Int?): Iterable<ClockAction> {
        LOGGER.debug("[GraphQL QUERY] clockActions(userId=$userId)")

        return userId?.let {
            storeService.getActions(it).orEmpty()
        } ?: storeService.getActions().orEmpty()
    }

    fun lastClockAction(userId: Int): ClockAction {
        LOGGER.debug("[GraphQL QUERY] lastClockAction(userId=$userId)")
        return storeService.getLastAction(userId)
    }
}