package org.teknux.webapp.service

import org.springframework.stereotype.Service
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.User
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

@Service
class StoreService {

    @Volatile
    private var currentUsersIndex: AtomicInteger = AtomicInteger(0)
    @Volatile
    var currentActionsIndex: AtomicInteger = AtomicInteger(0)

    private val usersMap: MutableMap<Int, User> = ConcurrentHashMap()
    private val actionsMap: MutableMap<Int, MutableSet<ClockAction>> = ConcurrentHashMap()

    @Synchronized
    fun newUser(user: User): User {
        usersMap.values.find { value -> value.name == user.name }?.let {
            throw IllegalArgumentException("User Name [${user.name}] already exist!")
        }

        user.id = currentUsersIndex.incrementAndGet();
        user.id!!.let {
            usersMap[it] = user
        }
        return user
    }

    @Synchronized
    fun removeUser(id: Int) {
        usersMap[id]?.let {
            actionsMap.remove(id)
            usersMap.remove(id)
        } ?: throw IllegalArgumentException("User Id does not exist!")
    }

    fun getUser(id: Int): User {
        usersMap[id]?.let {
            return it
        } ?: throw IllegalArgumentException("Unknown User Id [${id}]!")
    }

    fun getUsers(): Iterable<User> {
        return usersMap.values
    }

    @Synchronized
    fun addAction(action: ClockAction): ClockAction {
        if (action.userId in usersMap) {
            action.id = currentActionsIndex.incrementAndGet()
            action.timestamp = LocalDateTime.now()
            actionsMap[action.userId] = (actionsMap.getOrPut(action.userId) { mutableSetOf() } + action) as MutableSet
            return action
        } else {
            throw IllegalArgumentException("User Id for this Action does not exist!")
        }
    }

    fun getActions(): Set<ClockAction>? {
        return actionsMap.values.stream().flatMap(Set<ClockAction>::stream).collect(Collectors.toSet())
    }

    fun getActions(user: User?): Set<ClockAction>? {
        return user?.let { actionsMap[user.id] } ?: getActions()
    }

    fun getLastAction(user: User): ClockAction {
        return getActions(user).orEmpty().sortedByDescending { it.timestamp }.first()
    }

}