package com.softartdev.notedelight.shared.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.softartdev.notedelight.shared.db.NoteDb

class JdbcDatabaseTestHolder: DatabaseHolder() {
    override val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    init {
        NoteDb.Schema.create(driver)
    }

    override fun close() = driver.close()
}