package ru.debajo.reader.rss.data.converter

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import ru.debajo.reader.rss.ext.switchIfEmpty
import timber.log.Timber
import java.util.*

private val formatter: DateTimeFormatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z")

fun String.parseDateTimeSafe(): DateTime? {
    return runCatching {
        val fixedDate = when {
            endsWith("GMT", true) -> replace("GMT", "+0000", true)
            endsWith("UTC", true) -> replace("UTC", "+0000", true)
            else -> this
        }
        formatter.withLocale(Locale.US).parseDateTime(fixedDate)
    }
        .onFailure { Timber.tag("Date parsing error").e(it) }
        .switchIfEmpty { DateTime.parse(this) }
        .onFailure { Timber.tag("Date parsing error").e(it) }
        .getOrNull()
}


fun main() {
    print(formatter.withLocale(Locale.US).parseDateTime("Tue, 30 Nov 2021 14:13:20 +0000"))
}