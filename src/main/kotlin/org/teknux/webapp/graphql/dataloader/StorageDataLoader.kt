package org.teknux.webapp.graphql.dataloader

import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.teknux.webapp.service.Neo4jSessionFactory

open class StorageDataLoader<K, V>(neo4jSessionFactory: Neo4jSessionFactory, batchLoadFunction: BatchLoader<K, V>?, options: DataLoaderOptions?) : DataLoader<K, V>(batchLoadFunction, options)