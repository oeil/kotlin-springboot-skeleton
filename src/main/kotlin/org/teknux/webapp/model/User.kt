package org.teknux.webapp.model

import javax.persistence.*

@Entity
class User(
        @Id
        @Column(unique = true, nullable = false)
        var id: Int? = null,

        @Column(unique = true, nullable = false)
        var name: String,

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = [CascadeType.ALL] )
        var clockActions: MutableList<ClockAction>? = null
)