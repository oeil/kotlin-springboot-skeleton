package org.teknux.webapp.graphsql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService

class UserResolver(private val storeService: StoreService): GraphQLResolver<User> {

    fun getClockActions(user: User) : Iterable<ClockAction> {
        return storeService.getActions(user).orEmpty()
    }
}