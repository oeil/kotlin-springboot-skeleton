package org.teknux.webapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.neo4j.ogm.annotation.Relationship

data class Office(
        var id: Long? = null,
        var name: String? = null,
        @JsonIgnore
        @Relationship(direction = Relationship.INCOMING)
        var clockActions: MutableList<ClockAction>? = null
)