package org.teknux.webapp.service

import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.transaction.TransactionOptions
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.stereotype.Service
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.Paging
import org.teknux.webapp.model.User
import java.time.LocalDateTime
import java.util.stream.Collectors
import javax.annotation.PostConstruct


@Service
@Conditional(HazelcastStoreService.HazelcastCondition::class)
class HazelcastStoreService : IStoreService {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(HazelcastStoreService::class.java)

        @JvmStatic
        val OFFICE_MAP_NAME: String = "officeMap"
        @JvmStatic
        val OFFICE_ATOMIC_ID_NAME: String = "officeAtomicId"

        @JvmStatic
        val USER_MAP_NAME: String = "userMap"
        @JvmStatic
        val USER_ATOMIC_ID_NAME: String = "userAtomicId"

        @JvmStatic
        val ACTION_MAP_NAME: String = "actionMap"
        @JvmStatic
        val ACTION_ATOMIC_ID_NAME: String = "actionAtomicId"
    }

    lateinit var hazelcastInstance: HazelcastInstance

    fun <T> Paging.getPages(input: List<T>): List<T> {
        val start = offset * limit
        var end = (start) + limit

        if (start >= input.size - 1) return listOf()
        if (end > input.size - 1) end = input.size - 1

        return input.subList(offset * limit, (offset * limit) + limit)
    }

    fun <T> List<T>.pagesOrAll(paging: Paging?) = paging?.getPages(this) ?: this

    @PostConstruct
    fun init() {
        hazelcastInstance = Hazelcast.newHazelcastInstance()
    }

    override fun newOffice(office: Office): Office {
        LOGGER.trace("[${this.javaClass.simpleName}] newOffice([$office])")
        val officesMap = hazelcastInstance.getMap<Int, Office>(OFFICE_MAP_NAME)

        officesMap.values.find { value -> value.name == office.name }?.let {
            throw IllegalArgumentException("User Name [${office.name}] already exist!")
        }

        office.id = hazelcastInstance.getAtomicLong(OFFICE_ATOMIC_ID_NAME).incrementAndGet().toInt()
        office.id!!.let {
            officesMap[it] = office
        }

        return office
    }

    override fun getOffice(officeId: Int): Office {
        LOGGER.trace("[${this.javaClass.simpleName}] getOffice(officeId=[$officeId])")

        val officesMap = hazelcastInstance.getMap<Int, Office>(OFFICE_MAP_NAME)
        officesMap[officeId]?.let {
            return it
        } ?: throw IllegalArgumentException("Office Id does not exist!")
    }

    override fun getOffices(ids: Set<Int>?, paging: Paging?): List<Office> {
        LOGGER.trace("[${this.javaClass.simpleName}] getOffices(ids=[$ids], paging=[$paging])")
        val officesMap = hazelcastInstance.getMap<Int, Office>(OFFICE_MAP_NAME)
        return ids?.let {
            var result: MutableList<Office> = mutableListOf()
            it.forEach { result.add(officesMap[it]!!) }
            result.sortedBy { it.id }.pagesOrAll(paging)
        } ?: officesMap.values.toList().sortedBy { it.id }.pagesOrAll(paging)
    }

    override fun newUser(user: User): User {
        LOGGER.trace("[${this.javaClass.simpleName}] newUser(user=[$user])")

        val usersMap = hazelcastInstance.getMap<Int, User>(USER_MAP_NAME)
        usersMap.values.find { value -> value.name == user.name }?.let {
            throw IllegalArgumentException("User Name [${user.name}] already exist!")
        }

        user.id = hazelcastInstance.getAtomicLong(USER_ATOMIC_ID_NAME).incrementAndGet().toInt()
        user.id!!.let {
            usersMap[it] = user
        }
        return user
    }

    override fun getUser(id: Int): User {
        LOGGER.trace("[${this.javaClass.simpleName}] getUser(id=[$id])")

        val usersMap = hazelcastInstance.getMap<Int, User>(USER_MAP_NAME)
        usersMap[id]?.let {
            return it
        } ?: throw IllegalArgumentException("Unknown User Id [${id}]!")
    }

    override fun getUsers(paging: Paging?): List<User> {
        LOGGER.trace("[${this.javaClass.simpleName}] getUsers()")
        val usersMap = hazelcastInstance.getMap<Int, User>(USER_MAP_NAME)
        return usersMap.values.sortedBy { it.id }.toList().pagesOrAll(paging)
    }

    override fun addAction(action: ClockAction): ClockAction {
        LOGGER.trace("[${this.javaClass.simpleName}] addAction(action=[$action])")

        val context = hazelcastInstance.newTransactionContext(TransactionOptions().setTransactionType(TransactionOptions.TransactionType.ONE_PHASE))
        context.beginTransaction()

        val usersMap = context.getMap<Int, User>(USER_MAP_NAME)
        val officesMap = context.getMap<Int, Office>(OFFICE_MAP_NAME)
        val actionsMap = context.getMap<Int, MutableList<ClockAction>>(ACTION_MAP_NAME)

        if (action.user.id in usersMap.keySet()) {
            action.user = usersMap[action.user.id]

            action.office = officesMap[action.office.id]!!
            action.id = hazelcastInstance.getAtomicLong(ACTION_ATOMIC_ID_NAME).incrementAndGet().toInt()
            action.timestamp = LocalDateTime.now()

            if (action.user.clockActions == null) action.user.clockActions = mutableListOf() else action.user.clockActions?.add(action)
            actionsMap[action.user.id!!] = if (actionsMap.containsKey(action.user.id)) {
                (actionsMap.get(action.user.id!!) + action) as MutableList<ClockAction>
            } else {
                mutableListOf(action)
            }

            context.commitTransaction()
            return action
        } else {
            context.rollbackTransaction()
            throw IllegalArgumentException("User Id for this Action does not exist!")
        }
    }

    private fun fetchAllActions(paging: Paging? = null): List<ClockAction> {
        LOGGER.trace("[${this.javaClass.simpleName}] fetchAllActions()")
        val actionsMap = hazelcastInstance.getMap<Int, MutableList<ClockAction>>(ACTION_MAP_NAME)
        return actionsMap.values.stream().flatMap(MutableList<ClockAction>::stream).collect(Collectors.toList()).sortedBy { it.id }.pagesOrAll(paging)
    }

    override fun getActions(userId: Int, paging: Paging?): List<ClockAction> {
        LOGGER.trace("[${this.javaClass.simpleName}] getActions(user=[$userId])")
        val actionsMap = hazelcastInstance.getMap<Int, MutableList<ClockAction>>(ACTION_MAP_NAME)
        return actionsMap[userId]?.sortedBy { it.id }?.pagesOrAll(paging) ?: listOf()
    }

    override fun getActions(userIds: Collection<Int>?, paging: Paging?): List<ClockAction> {
        LOGGER.trace("[${this.javaClass.simpleName}] getActions(userIds=[$userIds])")
        return userIds?.let { fetchAllActions().filter { it.user.id in userIds }.sortedBy { it.id }.pagesOrAll(paging) }
                ?: fetchAllActions(paging)
    }

    override fun getLastAction(userId: Int): ClockAction {
        LOGGER.trace("[${this.javaClass.simpleName}] getLastAction(user=[$userId])")
        return getActions(userId).orEmpty().sortedByDescending { it.timestamp }.first()
    }

    class HazelcastCondition : Condition {
        override fun matches(context: ConditionContext?, metadata: AnnotatedTypeMetadata?): Boolean {
            return (System.getProperty(IStoreService.STORE_PROPERTY)
                    ?: IStoreService.DEFAULT_STORE).equals(IStoreService.HAZELCAST_STORE)
        }
    }
}