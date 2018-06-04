package org.teknux.webapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.util.*

data class ClockAction(
        val id: String = UUID.randomUUID().toString(),
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val type: Int,
        val desc: String = "",
        @JsonIgnore val user: User
)