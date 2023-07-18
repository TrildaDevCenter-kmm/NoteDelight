package com.softartdev.notedelight.shared.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.softartdev.notedelight.shared.db.NoteDb
import io.github.aakira.napier.Napier
import java.sql.SQLException
import java.util.Properties

class JdbcDatabaseHolder(props: Properties = Properties()) : DatabaseHolder() {
    override val driver = JdbcSqliteDriver(
        url = JdbcSqliteDriver.IN_MEMORY + DatabaseRepo.DB_NAME, // jdbc:sqlite:notes.db
        properties = props
    )
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    private var currentVersion: Int
        get() {
            val queryResult = driver.execute(null, "PRAGMA user_version;", 0, null)
            val ver: Long = queryResult.value
            return ver.toInt()
        }
        set(value) {
            driver.execute(null, "PRAGMA user_version = $value;", 0, null)
        }

    init {
        if (currentVersion == 0) {
            try {
                NoteDb.Schema.create(driver)
            } catch (sqlException: SQLException) {
                Napier.e("Error creating database", sqlException)
            }
            currentVersion = 1
        } else if (NoteDb.Schema.version > currentVersion) {
            NoteDb.Schema.migrate(driver, currentVersion.toLong(), NoteDb.Schema.version)
        }
    }

    override fun close() = driver.close()
}