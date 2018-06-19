package org.teknux.webapp.graphql.dataloader

import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.service.IStoreService

/**
 * GraphQL DataLoader to batch-fetch all [ClockAction] items for all given [User] ids - Spring component ready for dep injection support.
 */
@Component
class UsersToClockActionsDataLoader(storeService: IStoreService, options: DataLoaderOptions? = DataLoaderOptions.newOptions().setBatchingEnabled(true).setCachingEnabled(true)) : GenericValueListDataLoader<Int, ClockAction>(
        dataFetcher = { storeService.getActions(it).orEmpty() }, idSelector = { it.user.id!! }, options = options
)