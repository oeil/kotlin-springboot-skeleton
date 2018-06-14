package org.teknux.webapp.graphsql.resolver

import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import java.util.concurrent.CompletableFuture

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