package org.teknux.webapp.graphql.dataloader

import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.service.Neo4jSessionFactory
import org.teknux.webapp.service.Neo4jStoreService

/**
 * GraphQL DataLoader to batch-fetch all [ClockAction] items for all given [User] ids - Spring component ready for dep injection support.
 */
@Component
class UsersToClockActionsDataLoader(neo4jSessionFactory: Neo4jSessionFactory, options: DataLoaderOptions? = DataLoaderOptions.newOptions().setBatchingEnabled(true).setCachingEnabled(false)) : GenericValueListDataLoader<Long, ClockAction>(
        dataFetcher = { Neo4jStoreService(neo4jSessionFactory).init().getActions(userIds = it) }, idSelector = { it.user!!.id!! }, options = options
)