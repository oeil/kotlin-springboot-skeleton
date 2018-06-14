package org.teknux.webapp.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.springframework.stereotype.Component
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService

@Component
class Mutation(private val storeService: StoreService): GraphQLMutationResolver {

    fun office(name: String): Office {
        return storeService.newOffice(Office(name = name))
    }

    fun user(name: String): User {
        return storeService.newUser(User(name = name))
    }

    fun clockIn(userId: Int, desc: String?, officeId: Int): ClockAction {
        return storeService.addAction(ClockAction(userId = userId, type = 1, desc = desc ?: "in", officeId = officeId))
    }

    fun clockOut(userId: Int, desc: String?, officeId: Int): ClockAction {
        return storeService.addAction(ClockAction(userId = userId, type = 0, desc = desc ?: "out", officeId = officeId))
    }
}