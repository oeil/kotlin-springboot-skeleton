package org.teknux.webapp.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.User
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

@Service
class StoreService {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoreService::class.java)
    }

    @Volatile
    private var currentOfficeIndex: AtomicInteger = AtomicInteger(0)
    @Volatile
    private var currentUsersIndex: AtomicInteger = AtomicInteger(0)
    @Volatile
    var currentActionsIndex: AtomicInteger = AtomicInteger(0)

    private val officesMap: MutableMap<Int, Office> = ConcurrentHashMap()
    private val usersMap: MutableMap<Int, User> = ConcurrentHashMap()
    private val actionsMap: MutableMap<Int, MutableSet<ClockAction>> = ConcurrentHashMap()
    private val actionIdToOfficeIdMap: MutableMap<Int, Int> = ConcurrentHashMap()

    @Synchronized
    fun newOffice(office: Office): Office {
        LOGGER.debug("[StoreService] newOffice(user=[$office])")

        officesMap.values.find { value -> value.name == office.name }?.let {
            throw IllegalArgumentException("User Name [${office.name}] already exist!")
        }

        office.id = currentOfficeIndex.incrementAndGet();
        office.id!!.let {
            officesMap[it] = office
        }
        return office
    }

    @Synchronized
    fun removeOffice(officeId: Int) {
        LOGGER.debug("[StoreService] removeOffice(officeId=[$officeId])")

        officesMap[officeId]?.let {
            val actions = getActions().orEmpty().filter { it.officeId == officeId }
            val userIds = actions.groupBy { it.userId }.keys
            if (actions.isNotEmpty()) throw IllegalArgumentException("ClockActions refs exist for officeId=[$officeId] by userIds=[$userIds]") else officesMap.remove(officeId)
        } ?: throw IllegalArgumentException("User Id does not exist!")
    }

    fun getOffice(officeId: Int): Office {
        LOGGER.debug("[StoreService] getOffice(officeId=[$officeId])")

        officesMap[officeId]?.let {
            return it
        } ?: throw IllegalArgumentException("Office Id does not exist!")
    }

    fun getOffices(clockIds: Set<Int>? = null): List<Office> {
        LOGGER.debug("[StoreService] getOffices(officeId=[$clockIds])")
        val results: MutableList<Office> = mutableListOf()
        return clockIds?.let { it.forEach {
            actionIdToOfficeIdMap[it]?.let { results.add(officesMap[it]!!) } }
            results
        } ?: officesMap.values.toList()
    }

    @Synchronized
    fun newUser(user: User): User {
        LOGGER.debug("[StoreService] newUser(user=[$user])")

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
        LOGGER.debug("[StoreService] removeUser(id=[$id])")

        usersMap[id]?.let {
            actionsMap.remove(id)?.let {
                it.forEach { actionIdToOfficeIdMap.remove(it.id) }
            }
            usersMap.remove(id)
        } ?: throw IllegalArgumentException("User Id does not exist!")
    }

    fun getUser(id: Int): User {
        LOGGER.debug("[StoreService] getUser(id=[$id])")

        usersMap[id]?.let {
            return it
        } ?: throw IllegalArgumentException("Unknown User Id [${id}]!")
    }

    fun getUsers(): Iterable<User> {
        LOGGER.debug("[StoreService] getUsers()")
        return usersMap.values
    }

    @Synchronized
    fun addAction(action: ClockAction): ClockAction {
        LOGGER.debug("[StoreService] addAction(action=[$action])")

        if (action.userId in usersMap) {
            action.id = currentActionsIndex.incrementAndGet()
            action.timestamp = LocalDateTime.now()
            actionsMap[action.userId] = (actionsMap.getOrPut(action.userId) { mutableSetOf() } + action) as MutableSet
            actionIdToOfficeIdMap[action.id!!] = action.officeId!!
            return action
        } else {
            throw IllegalArgumentException("User Id for this Action does not exist!")
        }
    }

    private fun fetchAllActions(): Set<ClockAction>? {
        LOGGER.debug("[StoreService] fetchAllActions()")
        return actionsMap.values.stream().flatMap(Set<ClockAction>::stream).collect(Collectors.toSet())
    }

    fun getActions(userId: Int): Set<ClockAction>? {
        LOGGER.debug("[StoreService] getActions(user=[$userId])")
        return actionsMap[userId]
    }

    fun getActions(userIds: Iterable<Int>? = null): Set<ClockAction>? {
        LOGGER.debug("[StoreService] getActions(userIds=[$userIds])")
        return userIds?.let { fetchAllActions().orEmpty().filter { it.userId in userIds }.toSet() } ?: fetchAllActions()
    }

    fun getLastAction(userId: Int): ClockAction {
        LOGGER.debug("[StoreService] getLastAction(user=[$userId])")
        return getActions(userId).orEmpty().sortedByDescending { it.timestamp }.first()
    }

}