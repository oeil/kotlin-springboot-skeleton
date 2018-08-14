package org.teknux.webapp.service

import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.Paging
import org.teknux.webapp.model.User

interface IStoreService {

    fun newOffice(office: Office): Office

    fun getOffice(officeId: Long): Office

    fun getOffices(ids: Set<Long>? = null, paging: Paging? = null): List<Office>

    fun newUser(user: User): User

    fun getUser(id: Long): User

    fun getUsers(paging: Paging? = null): List<User>

    fun addAction(action: ClockAction): ClockAction

    fun getActions(userId: Long, paging: Paging? = null): List<ClockAction>

    fun getActions(userIds: Collection<Long>? = null, paging: Paging? = null): List<ClockAction>

    fun getLastAction(userId: Long): ClockAction

    fun countOffices(): Int

    fun countUsers(): Int

    fun countClockActions(): Int

    companion object {
        const val STORE_PROPERTY: String = "store"
        const val NEO4J_STORE: String = "neo4j"
        const val DEFAULT_STORE: String = NEO4J_STORE
    }
}