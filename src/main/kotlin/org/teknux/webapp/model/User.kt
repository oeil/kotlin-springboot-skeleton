package org.teknux.webapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class User(
        @Id
        @Column(unique = true, nullable = false)
        var id: Int? = null,

        @Column(unique = true, nullable = false)
        var name: String,

        @JsonIgnore
        @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = [CascadeType.ALL] )
        var clockActions: MutableList<ClockAction>? = null
)