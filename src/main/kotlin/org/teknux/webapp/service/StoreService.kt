package org.teknux.webapp.service

import org.springframework.stereotype.Service
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.User
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

@Service
class StoreService {

    private val usersMap: MutableMap<String, User> = ConcurrentHashMap()
    private val actionsMap: MutableMap<User, Set<ClockAction>> = ConcurrentHashMap()

    @Synchronized
    fun newUser(name: String): User {
        val user = User(name = name)
        usersMap[user.id] = user
        return user
    }

    @Synchronized
    fun removeUser(id: String) {
        usersMap[id]?.let {
            actionsMap.remove(it)
            usersMap.remove(id)
        }
    }

    fun getUser(id: String): User? {
        return usersMap[id]
    }

    fun getUsers(): Iterable<User> {
        return usersMap.values
    }

    @Synchronized
    fun addAction(action: ClockAction): ClockAction {
        actionsMap[action.user] = actionsMap.getOrPut(action.user) { setOf() } + action
        return action
    }

    fun getActions(): Set<ClockAction>? {
        return actionsMap.values.stream().flatMap(Set<ClockAction>::stream).collect(Collectors.toSet())
    }

    fun getActions(user: User): Set<ClockAction>? {
        return actionsMap[user]
    }

}