package org.teknux.webapp.service

import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.User

interface IStoreService {

    fun newOffice(office: Office): Office

    fun getOffice(officeId: Int): Office

    fun getOffices(clockIds: Set<Int>? = null): List<Office>

    fun newUser(user: User): User

    fun getUser(id: Int): User

    fun getUsers(): Iterable<User>

    fun addAction(action: ClockAction): ClockAction

    fun getActions(userId: Int): Set<ClockAction>?

    fun getActions(userIds: Iterable<Int>? = null): Set<ClockAction>?

    fun getLastAction(userId: Int): ClockAction

    companion object {
        val STORE_PROPERTY: String = "store"
        val IN_MEMORY_STORE: String = "memory"
        val H2_STORE: String = "h2"
        val DEFAULT_STORE: String = H2_STORE
    }
}