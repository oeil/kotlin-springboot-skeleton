package org.teknux.webapp.graphsql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService

class UserResolver(private val storeService: StoreService): GraphQLResolver<User> {

    fun getClockActions(user: User, type: Int?, containsDesc: String?) : Iterable<ClockAction> {
        return if (type == null && containsDesc == null) {
            storeService.getActions(user).orEmpty()
        } else {
            storeService.getActions(user).orEmpty().filter { action ->
                (type?.equals(action.type) ?: true) && (containsDesc?.let { action.desc.contains(it, true)} ?: true)
            }
        }
    }
}