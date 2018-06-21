package org.teknux.webapp.graphql.dataloader

import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import java.util.concurrent.CompletableFuture

/**
 * Implementation of GraphQL Data Loader that builds a custom Batch Loader using a dataFetcher returning an unsorted @Iterable<VALUE>,
 * Groups and Sort it by Keys to finally return a @List<List<VALUE>> which is what @DataLoader expects from @BatchLoader.
 *
 * This usually what's needed what fetching against traditional data-store (db, list, maps). This removes boilerplate code.
 *
 * @author oeil
 */
open class GenericValueListDataLoader<ID, VALUE>(dataFetcher: (Collection<ID>) -> Collection<VALUE>, idSelector: (VALUE) -> ID, options: DataLoaderOptions? = null) : DataLoader<ID, List<VALUE>>(
        BatchLoader { keys -> CompletableFuture.supplyAsync {
            val results: MutableList<List<VALUE>> = mutableListOf()
            val idToResultsMap: Map<ID, List<VALUE>> = dataFetcher.invoke(keys).groupBy { idSelector.invoke(it) }.map { it.key to it.value }.toMap()
            keys.forEach { idToResultsMap[it]?.let { results.add(it) } ?: results.add(emptyList()) }
            results
        }
}, options)