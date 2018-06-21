package org.teknux.webapp.graphql.dataloader

import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.stereotype.Component
import org.teknux.webapp.model.Office
import org.teknux.webapp.service.IStoreService
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class OfficeIdsToOfficesDataLoader(storeService: IStoreService, options: DataLoaderOptions? = DataLoaderOptions.newOptions().setBatchingEnabled(true).setCachingEnabled(false)) : DataLoader<Int, Office>(
        BatchLoader {
            CompletableFuture.supplyAsync(Supplier {
                val results: MutableList<Office> = mutableListOf()
                val offices = storeService.getOffices(it.toSet()).map { it.id to it }.toMap()
                it.forEach { results.add(offices[it]!!) }
                results
            }, SyncTaskExecutor()) //avoid spwaning new thread to run this query
        }, options
)