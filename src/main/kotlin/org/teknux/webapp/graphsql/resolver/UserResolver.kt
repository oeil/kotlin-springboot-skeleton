package org.teknux.webapp.graphsql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.dataloader.DataLoaderOptions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService
import java.util.concurrent.CompletableFuture

class UserResolver(private val storeService: StoreService): GraphQLResolver<User> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UserResolver::class.java)
    }

    @Autowired
    private lateinit var userToClockActionsDataLoader: UserToClockActionsDataLoader

    /**
     * Fetcher making use of GraphQL DataLoader to avoid n+1 queries when asking [ClockAction] collection per [User]
     *
     * @param storeService the service used to access data
     */
    fun getClockActions(user: User, type: Int?, containsDesc: String?): CompletableFuture<List<ClockAction>> {
        LOGGER.info("[GraphQL Resolver] getClockActions(user=[$user] type=[$type] containsDesc=[$containsDesc])")
        return userToClockActionsDataLoader.load(user.id).thenApplyAsync {
            it.filter { action ->
                //filter actions based on extra graphql
                (type?.equals(action.type) ?: true) && (containsDesc?.let { action.desc.contains(it, true)} ?: true)
            }
        };
    }

    /*
    /**
     * Simple Fetcher without DataLoader (batching of queries and/or caching)
     */
    fun getClockActions(user: User, type: Int?, containsDesc: String?) : Iterable<ClockAction> {
        LOGGER.info("[GraphQL Resolver] getClockActions(user=[${user}] type=[$type] containsDesc=[$containsDesc])")

        return if (type == null && containsDesc == null) {
            storeService.getActions(user).orEmpty()
        } else {
            storeService.getActions(user).orEmpty().filter { action ->
                (type?.equals(action.type) ?: true) && (containsDesc?.let { action.desc.contains(it, true)} ?: true)
            }
        }
    }
    */

    /**
     * GraphQL DataLoader class specs to query ClockActions for user - useful for springboot to be used as a Bean (dep injection).
     */
    class UserToClockActionsDataLoader(fetcher: (Iterable<Int>) -> Iterable<ClockAction>, keySelector: (ClockAction) -> Int, options: DataLoaderOptions? = null) : GenericValueListDataLoader<Int, ClockAction>(fetcher, keySelector, options)
}