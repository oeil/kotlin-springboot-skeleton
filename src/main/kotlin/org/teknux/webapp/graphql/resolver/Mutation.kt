package org.teknux.webapp.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.springframework.stereotype.Component
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.DataGenerator
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.User
import org.teknux.webapp.service.IStoreService

@Component
class Mutation(private val storeService: IStoreService) : GraphQLMutationResolver {

    fun office(name: String): Office {
        return storeService.newOffice(Office(name = name))
    }

    fun user(name: String): User {
        return storeService.newUser(User(name = name))
    }

    fun clockIn(userId: Int, desc: String?, officeId: Int): ClockAction {
        val user = storeService.getUser(userId);
        val office = storeService.getOffice(officeId);
        return storeService.addAction(ClockAction(user = user, type = 1, description = desc ?: "in", office = office))
    }

    fun clockOut(userId: Int, desc: String?, officeId: Int): ClockAction {
        val user = storeService.getUser(userId);
        val office = storeService.getOffice(officeId);
        return storeService.addAction(ClockAction(user = user, type = 0, description = desc ?: "out", office = office))
    }

    fun genData(offices: Int, users: Int, clockActions: Int) = DataGenerator(storeService).generate(offices, users, clockActions).toInt()
}