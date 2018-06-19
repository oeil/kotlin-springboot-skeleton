package org.teknux.webapp.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Office(
        @Id
        @Column(unique = true, nullable = false)
        var id: Int? = null,

        @Column(unique = true, nullable = false)
        var name: String?
)