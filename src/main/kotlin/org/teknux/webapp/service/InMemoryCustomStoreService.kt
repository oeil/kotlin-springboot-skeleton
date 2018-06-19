package org.teknux.webapp.service

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.stereotype.Service
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.User
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

@Service
@Conditional(InMemoryCustomStoreService.InMemoryCondition::class)
class InMemoryCustomStoreService : IStoreService {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(InMemoryCustomStoreService::class.java)
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

    @Synchronized
    override fun newOffice(office: Office): Office {
        LOGGER.trace("[InMemoryCustomStoreService] newOffice(user=[$office])")

        officesMap.values.find { value -> value.name == office.name }?.let {
            throw IllegalArgumentException("User Name [${office.name}] already exist!")
        }

        office.id = currentOfficeIndex.incrementAndGet();
        office.id!!.let {
            officesMap[it] = office
        }
        return office
    }

    /*
    @Synchronized
    fun removeOffice(officeId: Int) {
        LOGGER.trace("[InMemoryCustomStoreService] removeOffice(officeId=[$officeId])")

        officesMap[officeId]?.let {
            val actions = getActions().orEmpty().filter { it.officeId == officeId }
            val userIds = actions.groupBy { it.userId }.keys
            if (actions.isNotEmpty()) throw IllegalArgumentException("ClockActions refs exist for officeId=[$officeId] by userIds=[$userIds]") else officesMap.remove(officeId)
        } ?: throw IllegalArgumentException("User Id does not exist!")
    }
    */

    override fun getOffice(officeId: Int): Office {
        LOGGER.trace("[InMemoryCustomStoreService] getOffice(officeId=[$officeId])")

        officesMap[officeId]?.let {
            return it
        } ?: throw IllegalArgumentException("Office Id does not exist!")
    }

    override fun getOffices(ids: Set<Int>?): List<Office> {
        LOGGER.trace("[InMemoryCustomStoreService] getOffices(officeId=[$ids])")
        val results: MutableList<Office> = mutableListOf()
        return ids?.let {
            var result: MutableList<Office> = mutableListOf()
            it.forEach { result.add(officesMap[it]!!) }
            result
        } ?: officesMap.values.toList()
    }

    @Synchronized
    override fun newUser(user: User): User {
        LOGGER.trace("[InMemoryCustomStoreService] newUser(user=[$user])")

        usersMap.values.find { value -> value.name == user.name }?.let {
            throw IllegalArgumentException("User Name [${user.name}] already exist!")
        }

        user.id = currentUsersIndex.incrementAndGet();
        user.id!!.let {
            usersMap[it] = user
        }
        return user
    }

    /*
    @Synchronized
    fun removeUser(id: Int) {
        LOGGER.trace("[InMemoryCustomStoreService] removeUser(id=[$id])")

        usersMap[id]?.let {
            actionsMap.remove(id)?.let {
                it.forEach { actionIdToOfficeIdMap.remove(it.id) }
            }
            usersMap.remove(id)
        } ?: throw IllegalArgumentException("User Id does not exist!")
    }
    */

    override fun getUser(id: Int): User {
        LOGGER.trace("[InMemoryCustomStoreService] getUser(id=[$id])")

        usersMap[id]?.let {
            return it
        } ?: throw IllegalArgumentException("Unknown User Id [${id}]!")
    }

    override fun getUsers(): Iterable<User> {
        LOGGER.trace("[InMemoryCustomStoreService] getUsers()")
        return usersMap.values
    }

    @Synchronized
    override fun addAction(action: ClockAction): ClockAction {
        LOGGER.trace("[InMemoryCustomStoreService] addAction(action=[$action])")

        if (action.user.id in usersMap) {
            action.user = usersMap[action.user.id]!!


            action.office = officesMap[action.office.id]!!
            action.id = currentActionsIndex.incrementAndGet()
            action.timestamp = LocalDateTime.now()

            if (action.user.clockActions == null) action.user.clockActions = mutableListOf() else action.user.clockActions?.add(action)
            actionsMap[action.user.id!!] = (actionsMap.getOrPut(action.user.id!!) { mutableSetOf() } + action) as MutableSet

            return action
        } else {
            throw IllegalArgumentException("User Id for this Action does not exist!")
        }
    }

    private fun fetchAllActions(): Set<ClockAction>? {
        LOGGER.trace("[InMemoryCustomStoreService] fetchAllActions()")
        return actionsMap.values.stream().flatMap(Set<ClockAction>::stream).collect(Collectors.toSet())
    }

    override fun getActions(userId: Int): Set<ClockAction>? {
        LOGGER.trace("[InMemoryCustomStoreService] getActions(user=[$userId])")
        return actionsMap[userId]
    }

    override fun getActions(userIds: Iterable<Int>?): Set<ClockAction>? {
        LOGGER.trace("[InMemoryCustomStoreService] getActions(userIds=[$userIds])")
        return userIds?.let { fetchAllActions().orEmpty().filter { it.user.id in userIds }.toSet() } ?: fetchAllActions()
    }

    override fun getLastAction(userId: Int): ClockAction {
        LOGGER.trace("[InMemoryCustomStoreService] getLastAction(user=[$userId])")
        return getActions(userId).orEmpty().sortedByDescending { it.timestamp }.first()
    }

    class InMemoryCondition : Condition {
        override fun matches(context: ConditionContext?, metadata: AnnotatedTypeMetadata?): Boolean {
            return (System.getProperty(IStoreService.STORE_PROPERTY) ?: IStoreService.DEFAULT_STORE).equals(IStoreService.IN_MEMORY_STORE)
        }
    }
}