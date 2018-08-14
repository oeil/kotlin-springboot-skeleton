package org.teknux.webapp.model

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.time.LocalDateTime

@NodeEntity
data class ClockAction(
        @Id @GeneratedValue
        var id: Long? = null,
        var timestamp: LocalDateTime? = null,
        var type: Int? = null,
        var description: String? = "",
        @Relationship(direction = Relationship.OUTGOING)
        var user: User? = null,
        @Relationship(direction = Relationship.OUTGOING)
        var office: Office? = null
)