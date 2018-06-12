package org.teknux.webapp.graphsql.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService

class Mutation(private val storeService: StoreService): GraphQLMutationResolver {

    fun user(name: String): User {
        return storeService.newUser(User(name = name))
    }

    fun clockIn(userId: Int, desc: String?): ClockAction {
        return storeService.addAction(ClockAction(userId = userId, type = 1, desc = desc ?: "in"))
    }

    fun clockOut(userId: Int, desc: String?): ClockAction {
        return storeService.addAction(ClockAction(userId = userId, type = 0, desc = desc ?: "out"))
    }
}