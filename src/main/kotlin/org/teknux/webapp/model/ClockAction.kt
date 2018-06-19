package org.teknux.webapp.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class ClockAction(
        @Id
        @Column(unique = true, nullable = false)
        var id: Int? = null,

        @Temporal(TemporalType.TIMESTAMP)
        var timestamp: LocalDateTime? = null,

        var type: Int,

        var description: String = "",

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "office_id")
        var office: Office
)