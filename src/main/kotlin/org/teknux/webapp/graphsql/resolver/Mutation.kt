package org.teknux.webapp.graphsql.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService

class Mutation(private val storeService: StoreService): GraphQLMutationResolver {

    fun user(name: String): User {
        return storeService.newUser(User(name = name))
    }


}