package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.ColumnAdapter
import platform.Foundation.NSDate
import kotlin.system.getTimeMillis

actual class Date actual constructor(timeInMillis: Long) {
    val nsDate = NSDate(
        timeIntervalSinceReferenceDate = timeInMillis.toDouble() / 1000
    )
    actual constructor() : this(getSystemTimeInMillis())
}

actual class DateAdapter actual constructor() : ColumnAdapter<Date, Long> {
    override fun decode(databaseValue: Long): Date = Date(databaseValue)
    override fun encode(value: Date): Long = value.nsDate.timeIntervalSinceReferenceDate.toLong() * 1000L
}

actual fun getSystemTimeInMillis(): Long = getTimeMillis()