package org.teknux.webapp.service

import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.Paging
import org.teknux.webapp.model.User

interface IStoreService {

    fun newOffice(office: Office): Office

    fun getOffice(officeId: Int): Office

    fun getOffices(ids: Set<Int>? = null, paging: Paging? = null): List<Office>

    fun newUser(user: User): User

    fun getUser(id: Int): User

    fun getUsers(paging: Paging? = null): List<User>

    fun addAction(action: ClockAction): ClockAction

    fun getActions(userId: Int, paging: Paging? = null): List<ClockAction>

    fun getActions(userIds: Collection<Int>? = null, paging: Paging? = null): List<ClockAction>

    fun getLastAction(userId: Int): ClockAction

    companion object {
        val STORE_PROPERTY: String = "store"
        val IN_MEMORY_STORE: String = "memory"
        val H2_STORE: String = "h2"
        val HAZELCAST_STORE: String = "hazelcast"
        val DEFAULT_STORE: String = H2_STORE
    }
}