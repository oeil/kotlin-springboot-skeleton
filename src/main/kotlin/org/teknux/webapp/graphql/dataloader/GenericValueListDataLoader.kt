package org.teknux.webapp.graphql.dataloader

import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import java.util.concurrent.CompletableFuture

/**
 * Implementation of GraphQL Data Loader that builds a custom Batch Loader using a fetcher returning an unsorted @Iterable<V>,
 * Groups and Sort it by Keys to finally return a @List<List<V>> which is what @DataLoader expects from @BatchLoader.
 *
 * This usually what's needed what fetching against traditional data-store (db, list, maps). This removes boilerplate code.
 * @author oeil
 */
open class GenericValueListDataLoader<K, V> : DataLoader<K, List<V>> {

    constructor(fetcher: (Iterable<K>) -> Iterable<V>, keySelector: (V) -> K, options: DataLoaderOptions? = null)
            : super(BatchLoader { keys ->
                CompletableFuture.supplyAsync {
                    var results: MutableList<List<V>> = mutableListOf()
                    val idToResultsMap: Map<K, List<V>> = fetcher.invoke(keys).groupBy { keySelector.invoke(it) }.map { it.key to it.value }.toMap()
                    keys.forEach { idToResultsMap[it]?.let { results.add(it) } ?: results.add(emptyList()) }
            results
            }
    }, options)

}