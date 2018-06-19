package org.teknux.webapp.model

import org.slf4j.LoggerFactory
import org.teknux.webapp.service.IStoreService
import org.teknux.webapp.util.StopWatch
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit

class DataGenerator(private val storeService: IStoreService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DataGenerator::class.java)
    }

    fun generate(offices: Int, users: Int, clockActionPairPerUser: Int): Long {
        LOGGER.debug("### Generating Data ###")
        val stopWatch = StopWatch().start()

        val officesSet: MutableList<Office> = mutableListOf()
        for (n in 1..offices) {
            var office = storeService.newOffice(Office(name = "Office_${UUID.randomUUID()}"))
            officesSet.add(office)
            LOGGER.trace("Office ${office.name} created : ${office.id}")
        }

        for (n in 1..users) {
            var user = storeService.newUser(User(name = "user_${UUID.randomUUID()}"))
            LOGGER.trace("User ${user.name} created : ${user.id}")
            user.id?.let {
                for (clockActionN in 1..clockActionPairPerUser) {
                    val randomOfficeIndex: Int = SecureRandom().nextInt(offices - 1)
                    storeService.addAction(ClockAction(type = 1, description = "clock in (${UUID.randomUUID()})", user = user, office = officesSet.get(randomOfficeIndex)))
                    LOGGER.trace("${user.name} clocked-in")
                    storeService.addAction(ClockAction(type = 0, description = "clock out (${UUID.randomUUID()})", user = user, office = officesSet.get(randomOfficeIndex)))
                    LOGGER.trace("${user.name} clocked-out")
                }
            }
        }

        val duration = stopWatch.elapsed(TimeUnit.SECONDS)
        LOGGER.debug("### Done Generated Data in ${duration}s - Offices=[$offices] Users=[$users] ClockActions=[${clockActionPairPerUser * 2 * users}]")
        return duration
    }
}