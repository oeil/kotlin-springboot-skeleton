package org.teknux.webapp.service

import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import org.springframework.stereotype.Component
import org.teknux.webapp.model.User
import java.nio.file.Paths

@Component
class Neo4jSessionFactory() : SessionFactory(Configuration.Builder().uri("file://${Paths.get(System.getProperty("java.io.tmpdir")).resolve("neo4j-db-kotlin-skeleton").toFile().absoluteFile}").build(),
        User::class.java.`package`.name)