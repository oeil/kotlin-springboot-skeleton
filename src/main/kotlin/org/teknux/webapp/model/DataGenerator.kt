package org.teknux.webapp.model

import org.slf4j.LoggerFactory
import org.teknux.webapp.service.StoreService
import java.security.SecureRandom
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class DataGenerator(private val storeService: StoreService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DataGenerator::class.java)
    }

    fun generate(offices: Int, users: Int, clockActionPairPerUser: Int): Duration {
        LOGGER.debug("### Generating Data ###")
        var startTime = LocalDateTime.now()

        for (n in 1..offices) {
            var office = storeService.newOffice(Office(name = "Office_${UUID.randomUUID()}"))
            LOGGER.trace("Office ${office.name} created : ${office.id}")
        }

        for (n in 1..users) {
            var user = storeService.newUser(User(name = "user_${UUID.randomUUID()}"))
            LOGGER.trace("User ${user.name} created : ${user.id}")
            user.id?.let {
                for (clockActionN in 1..clockActionPairPerUser) {
                    val min = 1
                    val randomOfficeId: Int = SecureRandom().nextInt((offices - min) + 1) + min
                    storeService.addAction(ClockAction(type = 1, desc = "clock in (${UUID.randomUUID()})", userId = it, officeId = randomOfficeId))
                    LOGGER.trace("${user.name} clocked-in")
                    storeService.addAction(ClockAction(type = 0, desc = "clock out (${UUID.randomUUID()})", userId = it, officeId = randomOfficeId))
                    LOGGER.trace("${user.name} clocked-out")
                }
            }
        }

        val duration = Duration.between(startTime, LocalDateTime.now())
        LOGGER.debug("### Done Generated Data in ${duration.seconds}s - Offices=[$offices] Users=[$users] ClockActions=[${clockActionPairPerUser * 2 * users}]")
        return duration
    }
}