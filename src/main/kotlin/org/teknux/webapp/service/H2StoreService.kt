package org.teknux.webapp.service

import io.ebean.Ebean
import io.ebean.EbeanServerFactory
import io.ebean.config.ServerConfig
import org.avaje.datasource.DataSourceConfig
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.stereotype.Service
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.model.Office
import org.teknux.webapp.model.User
import org.teknux.webapp.model.query.QClockAction
import org.teknux.webapp.model.query.QOffice
import org.teknux.webapp.model.query.QUser
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct

@Service
@Conditional(H2StoreService.H2Condition::class)
class H2StoreService : IStoreService {

    override fun newOffice(office: Office): Office {
        Ebean.save(office)
        return office
    }

    override fun getOffice(officeId: Int): Office {
        return QOffice().id.eq(officeId).select().findOne() ?: throw IllegalArgumentException("Office Id does not exist!")
    }

    override fun getOffices(clockIds: Set<Int>?): List<Office> {
        return clockIds?.let { QOffice().id.isIn(clockIds).findList() } ?: QOffice().findList()
    }

    override fun newUser(user: User): User {
        Ebean.save(user)
        return user
    }

    override fun getUser(id: Int): User {
        return QUser().id.eq(id).findOne() ?: throw IllegalArgumentException("User Id does not exist!")
    }

    override fun getUsers(): Iterable<User> {
        return QUser().findList()
    }

    override fun addAction(action: ClockAction): ClockAction {
        action.timestamp = LocalDateTime.now()
        action.user = getUser(action.user.id!!)
        action.office = getOffice(action.office.id!!)
        Ebean.save(action)
        return action
    }

    override fun getActions(userId: Int): Set<ClockAction>? {
        return QClockAction().user.id.eq(userId).findList().toSet()
    }

    override fun getActions(userIds: Iterable<Int>?): Set<ClockAction>? {
        return QClockAction().user.id.isIn(userIds!! as Collection<Int>).findList().toSet()
    }

    override fun getLastAction(userId: Int): ClockAction {
        return QClockAction().where().user.id.eq(userId).orderBy().timestamp.desc().setMaxRows(1).findOne() ?: throw IllegalArgumentException("No Clock Action for user")
    }

    @PostConstruct
    fun init() {
        //read ebean config & create server & migrate database when necessary
        val dataSourceConfig = DatasourceConfigFactory.get().setMemoryDb(false).build()
        val serverConfig = ServerConfigFactory.build(dataSourceConfig, true, 10000)

        //manually register Entities Package for the runnable Fat JAR/WAR (ebean search classpath issue)
        val packages = ArrayList<String>()
        packages.add(ClockAction::class.java.getPackage().name)
        serverConfig.packages = packages

        //builds the server and runs db migration
        EbeanServerFactory.create(serverConfig)
    }

    /**
     * @author Francois EYL
     */
    class DatasourceConfigFactory private constructor() {

        private var dbFilePath: String? = null
        private var inMemory: Boolean = false

        init {
            dbFilePath = System.getProperty("dbPath") ?: "./h2"
            inMemory = true
        }

        fun build(): DataSourceConfig {
            val datasource = DataSourceConfig()
            datasource.driver = DRIVER
            datasource.username = DEFAULT_USER
            datasource.password = DEFAULT_PWD
            datasource.heartbeatSql = HEARTBEAT_SQL
            datasource.url = if (inMemory) URL_MEM else String.format(URL_FILE, dbFilePath)
            return datasource
        }

        fun setDbFilePath(path: String): DatasourceConfigFactory {
            this.dbFilePath = path
            return this
        }

        fun setMemoryDb(isMemory: Boolean): DatasourceConfigFactory {
            this.inMemory = isMemory
            return this
        }

        companion object {

            private val DRIVER = "org.h2.Driver"
            private val URL_MEM = "jdbc:h2:mem:tests;DB_CLOSE_DELAY=0"
            private val URL_FILE = "jdbc:h2:%s;AUTO_SERVER=TRUE"
            private val DEFAULT_USER = "sa"
            private val DEFAULT_PWD = ""
            private val HEARTBEAT_SQL = "select 1"

            fun get(): DatasourceConfigFactory {
                return DatasourceConfigFactory()
            }
        }
    }

    object ServerConfigFactory {

        fun build(dataSourceConfig: DataSourceConfig, runMigration: Boolean, batchSize: Int = 1000): ServerConfig {
            val serverConfig = ServerConfig()
            serverConfig.name = "db"

            serverConfig.loadFromProperties()

            serverConfig.queryBatchSize = batchSize
            serverConfig.lazyLoadBatchSize = batchSize
            serverConfig.databaseSequenceBatchSize = batchSize

            serverConfig.isDdlRun = true
            serverConfig.isDdlGenerate = true
            serverConfig.isDdlCreateOnly = false

            val dbMigrationConfig = serverConfig.migrationConfig
            dbMigrationConfig.isRunMigration = runMigration
            serverConfig.migrationConfig = dbMigrationConfig

            serverConfig.isDefaultServer = true
            serverConfig.isRegister = true
            serverConfig.dataSourceConfig = dataSourceConfig

            return serverConfig
        }
    }

    class H2Condition : Condition {
        override fun matches(context: ConditionContext?, metadata: AnnotatedTypeMetadata?): Boolean {
            return (System.getProperty(IStoreService.STORE_PROPERTY) ?: IStoreService.DEFAULT_STORE).equals(IStoreService.H2_STORE)
        }
    }
}