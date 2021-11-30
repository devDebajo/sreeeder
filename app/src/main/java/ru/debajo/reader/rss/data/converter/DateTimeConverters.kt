package ru.debajo.reader.rss.data.converter

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import ru.debajo.reader.rss.ext.switchIfEmpty
import java.util.*

private val formatter: DateTimeFormatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z");

fun String.parseDateTimeSafe(): DateTime? {
    return runCatching { formatter.withLocale(Locale.US).parseDateTime(this) }
        .switchIfEmpty { DateTime.parse(this) }
        .getOrNull()
}
