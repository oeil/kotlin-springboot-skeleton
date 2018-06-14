package org.teknux.webapp.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService

@Component
class Query(private val storeService: StoreService) : GraphQLQueryResolver {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Query::class.java)
    }

    fun offices(id: Int?): Iterable<Office> {
        LOGGER.debug("[GraphQL QUERY] offices(id=$id)")
        return storeService.getOffices(id?.let { setOf(id) })
    }

    fun users(id: Int?): Iterable<User> {
        LOGGER.debug("[GraphQL QUERY] users(id=$id)")

        return id?.let {
            setOf(storeService.getUser(id))
        } ?: storeService.getUsers()
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