package org.teknux.webapp.graphql.dataloader

import org.dataloader.BatchLoader
import org.dataloader.DataLoaderOptions
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.stereotype.Component
import org.teknux.webapp.model.Office
import org.teknux.webapp.service.IStoreService
import org.teknux.webapp.service.Neo4jSessionFactory
import org.teknux.webapp.service.Neo4jStoreService
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class OfficeIdsToOfficesDataLoader(neo4jSessionFactory: Neo4jSessionFactory, options: DataLoaderOptions? = DataLoaderOptions.newOptions().setBatchingEnabled(true).setCachingEnabled(false)) : StorageDataLoader<Long, Office>(neo4jSessionFactory,
        BatchLoader {
            CompletableFuture.supplyAsync(Supplier {
                var storeService: IStoreService = Neo4jStoreService(neo4jSessionFactory).init()
                val results: MutableList<Office> = mutableListOf()
                val offices = storeService.getOffices(it.toSet()).map { it.id to it }.toMap()
                it.forEach { results.add(offices[it]!!) }
                results
            }, SyncTaskExecutor()) //avoid spwaning new thread to run this query
        }, options
)