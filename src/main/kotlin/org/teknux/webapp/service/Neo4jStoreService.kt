package org.teknux.webapp.service

import org.neo4j.ogm.session.Session
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.stereotype.Service
import org.springframework.web.context.annotation.RequestScope
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.Paging
import org.teknux.webapp.model.User
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct


@Service
@Conditional(Neo4jStoreService.Neo4jCondition::class)
@RequestScope
class Neo4jStoreService(val factory: Neo4jSessionFactory) : IStoreService {

    class Neo4jCondition : Condition {
        override fun matches(context: ConditionContext?, metadata: AnnotatedTypeMetadata?): Boolean {
            return (System.getProperty(IStoreService.STORE_PROPERTY)
                    ?: IStoreService.DEFAULT_STORE).equals(IStoreService.NEO4J_STORE)
        }
    }

    private lateinit var session: Session

    @PostConstruct
    fun init(): Neo4jStoreService {
        session = factory.openSession()
        return this
    }

    fun <T> Session.queryPages(objectType:Class<T>, query:String, paging: Paging?, parameters:Map<String, *>? = null): List<T> {
        val pQuery = query + (paging?.let { " SKIP ${it.page * it.limit} LIMIT ${it.limit}" } ?: "")
        return this.query(objectType, pQuery, parameters ?: HashMap<String, Any>()).toList()
    }

    override fun newOffice(office: Office): Office {
        session.beginTransaction().use { tx ->
            session.save(office)
            tx.commit()
        }
        return office
    }

    override fun getOffice(officeId: Long): Office {
        return session.load(Office::class.java, officeId)
    }

    override fun getOffices(ids: Set<Long>?, paging: Paging?): List<Office> {
        val query = ids?.let { "MATCH (o:${Office::class.java.simpleName}) WHERE id(o) IN {ids} RETURN o ORDER BY o.id" } ?: "MATCH (o:${Office::class.java.simpleName}) RETURN o ORDER BY o.id"
        return session.queryPages(Office::class.java, query, paging, mapOf("ids" to ids))
    }

    override fun newUser(user: User): User {
        session.beginTransaction().use { tx ->
            session.save(user)
            tx.commit()
        }
        return user
    }

    override fun getUser(id: Long): User {
        return session.load(User::class.java, id)
    }

    override fun getUsers(paging: Paging?): List<User> {
        return session.queryPages(User::class.java, "MATCH (u:${User::class.java.simpleName}) RETURN u ORDER BY u.id", paging)
    }

    override fun addAction(action: ClockAction): ClockAction {
        session.beginTransaction().use { tx ->

            action.timestamp = LocalDateTime.now()
            action.user = getUser(action.user!!.id!!)
            action.office = getOffice(action.office!!.id!!)
            session.save(action)

            tx.commit()
        }
        return action
    }

    override fun getActions(userId: Long, paging: Paging?): List<ClockAction> {
        return getActions(setOf(userId), paging)
    }

    override fun getActions(userIds: Collection<Long>?, paging: Paging?): List<ClockAction> {
        val query = userIds?.let { "MATCH p=((u:${User::class.java.simpleName})-[ur]-(c:${ClockAction::class.java.simpleName})-[or]-()) WHERE id(u) IN {ids} RETURN p, rels(p) ORDER BY c.id DESC" }
                ?: "MATCH p=(a:${ClockAction::class.java.simpleName})-[r]-() RETURN nodes(p), rels(p) ORDER BY a.id DESC"
        return session.queryPages(ClockAction::class.java, query, paging, mapOf("ids" to userIds))
    }

    override fun getLastAction(userId: Long): ClockAction {
        val query = "MATCH p=((u:${User::class.java.simpleName})-[ur]-(c:${ClockAction::class.java.simpleName})-[or]-()) WHERE id(u) = {id} RETURN p, rels(p) ORDER BY c.timestamp DESC"
        return session.queryPages(ClockAction::class.java, query, Paging(limit = 1, page = 0), mapOf("id" to userId)).first()
    }

    override fun countOffices(): Int {
        return session.countEntitiesOfType(Office::class.java).toInt()
    }

    override fun countUsers(): Int {
        return session.countEntitiesOfType(User::class.java).toInt()
    }

    override fun countClockActions(): Int {
        return session.countEntitiesOfType(ClockAction::class.java).toInt()
    }
}