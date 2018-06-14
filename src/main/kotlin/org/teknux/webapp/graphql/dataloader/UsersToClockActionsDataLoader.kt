package org.teknux.webapp.graphql.dataloader

import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.service.StoreService

/**
 * GraphQL DataLoader to batch-fetch all [ClockAction] items for all given [User] ids - Spring component ready for dep injection support.
 */
@Component
class UsersToClockActionsDataLoader(storeService: StoreService, options: DataLoaderOptions? = null) : GenericValueListDataLoader<Int, ClockAction>(fetcher = { storeService.getActions(it).orEmpty() }, keySelector = { it.userId }, options = options)