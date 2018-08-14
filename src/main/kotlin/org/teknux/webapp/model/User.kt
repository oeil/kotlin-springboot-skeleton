package org.teknux.webapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.neo4j.ogm.annotation.*

@NodeEntity
class User(
        @Id @GeneratedValue
        var id: Long? = null,
        @Index(unique = true)
        var name: String? = null,
        @JsonIgnore
        @Relationship(direction = Relationship.INCOMING)
        var clockActions: MutableList<ClockAction>? = null
)