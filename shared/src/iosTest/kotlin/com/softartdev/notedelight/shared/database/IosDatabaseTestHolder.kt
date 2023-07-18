package com.softartdev.notedelight.shared.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import com.softartdev.notedelight.shared.db.NoteDb
import com.softartdev.notedelight.shared.db.NoteQueries

class IosDatabaseTestHolder(
    key: String? = null,
    rekey: String? = null,
    name: String = DatabaseRepo.DB_NAME,
    schema: SqlSchema<QueryResult.Value<Unit>> = NoteDb.Schema
) : DatabaseHolder() {
    private val configuration = DatabaseConfiguration(
        name = name,
        version = schema.version.toInt(),
        create = { connection ->
            wrapConnection(connection) { schema.create(it) }
        },
        upgrade = { connection, oldVersion, newVersion ->
            wrapConnection(connection) { schema.migrate(it, oldVersion.toLong(),
                newVersion.toLong()
            ) }
        },
        inMemory = true,
        encryptionConfig = DatabaseConfiguration.Encryption(
            key = key,
            rekey = rekey,
        )
    )
    override val driver: SqlDriver = NativeSqliteDriver(configuration)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries: NoteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}