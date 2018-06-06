package org.teknux.webapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime

data class ClockAction(
        var id: Int? = null,
        var timestamp: LocalDateTime? = null,
        var type: Int,
        var desc: String = "",
        val userId: Int
)