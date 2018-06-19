package org.teknux.webapp.graphql.dataloader

import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import org.teknux.webapp.model.Office
import org.teknux.webapp.service.IStoreService
import java.util.concurrent.CompletableFuture

@Component
class OfficeIdsToOfficesDataLoader (storeService: IStoreService, options: DataLoaderOptions? = DataLoaderOptions.newOptions().setBatchingEnabled(true).setCachingEnabled(true)) : DataLoader<Int, Office>(
        BatchLoader {
                CompletableFuture.supplyAsync { storeService.getOffices(it.toSet()) }
        }, options
)