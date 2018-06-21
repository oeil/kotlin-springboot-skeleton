package org.teknux.webapp.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.Paging
import org.teknux.webapp.model.User
import org.teknux.webapp.service.IStoreService
import org.teknux.webapp.util.StopWatch
import java.util.concurrent.TimeUnit

@Component
class Query(private val storeService: IStoreService) : GraphQLQueryResolver {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Query::class.java)
    }

    fun offices(id: Int?, paging: Paging?): Iterable<Office> {
        val stopWatch = StopWatch().start()
        val result = storeService.getOffices(id?.let { setOf(id) }, paging)
        LOGGER.debug("[GraphQL QUERY] offices(id=$id) - (processed in ${stopWatch.elapsed(TimeUnit.SECONDS)}s)")
        return result
    }

    fun users(id: Int?, paging: Paging?): Iterable<User> {
        val stopWatch = StopWatch().start()

        val result = id?.let {
            setOf(storeService.getUser(id))
        } ?: storeService.getUsers(paging)

        LOGGER.debug("[GraphQL QUERY] users(id=$id) - (processed in ${stopWatch.elapsed(TimeUnit.SECONDS)}s)")
        return result
    }

    fun clockActions(userId: Int?, paging: Paging?): Iterable<ClockAction> {
        val stopWatch = StopWatch().start()

        val result = userId?.let {
            storeService.getActions(it, paging)
        } ?: storeService.getActions(paging = paging)

        LOGGER.debug("[GraphQL QUERY] clockActions(userId=$userId) - (processed in ${stopWatch.elapsed(TimeUnit.SECONDS)}s)")
        return result
    }

    fun lastClockAction(userId: Int): ClockAction {
        var stopWatch = StopWatch().start()
        val result = storeService.getLastAction(userId)

        LOGGER.debug("[GraphQL QUERY] lastClockAction(userId=$userId) - (processed in ${stopWatch.elapsed(TimeUnit.SECONDS)}s)")
        return result
    }
}