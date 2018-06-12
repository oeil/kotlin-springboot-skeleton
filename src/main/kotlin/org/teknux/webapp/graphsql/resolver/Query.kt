package org.teknux.webapp.graphsql.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService

class Query(private val storeService: StoreService): GraphQLQueryResolver {

    fun users(id: Int?): Iterable<User> {
        id?.let {
            return setOf(storeService.getUser(id))
        } ?: return storeService.getUsers()
    }

    fun clockActions(userId: Int?): Iterable<ClockAction> {
        return userId?.let {
            storeService.getActions(storeService.getUser(it)).orEmpty()
        } ?: storeService.getActions().orEmpty()
    }

    fun lastClockAction(userId: Int): ClockAction {
        return storeService.getLastAction(storeService.getUser(userId))
    }
}