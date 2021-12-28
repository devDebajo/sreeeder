package ru.debajo.reader.rss.data.converter

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import timber.log.Timber
import java.util.*

private val parsers: List<DateTimeParser> = listOf(
    DateTimeParser.Format("EEE, dd MMM yyyy HH:mm:ss Z"),
    DateTimeParser.Format("dd MMM yyyy HH:mm:ss Z"),
    DateTimeParser.Default,
)

fun String.parseDateTimeSafe(): DateTime? {
    val fixedDate = when {
        endsWith("GMT", true) -> replace("GMT", "+0000", true)
        endsWith("UTC", true) -> replace("UTC", "+0000", true)
        else -> this
    }
    return parsers
        .asSequence()
        .mapNotNull { it.parseSafe(fixedDate, Locale.US) }
        .firstOrNull()
}

internal sealed interface DateTimeParser {
    fun parseSafe(raw: String, locale: Locale): DateTime? {
        return runCatching { parseUnsafe(raw, locale) }
            .onFailure { Timber.tag("Date parsing error").e(it) }
            .getOrNull()
    }

    fun parseUnsafe(raw: String, locale: Locale): DateTime

    object Default : DateTimeParser {
        private val formatter: DateTimeFormatter by lazy {
            ISODateTimeFormat.dateTimeParser().withOffsetParsed()
        }

        override fun parseUnsafe(raw: String, locale: Locale): DateTime {
            return DateTime.parse(raw, formatter.withLocale(locale))
        }
    }

    class Format(pattern: String) : DateTimeParser {
        private val formatter: DateTimeFormatter by lazy { DateTimeFormat.forPattern(pattern) }

        override fun parseUnsafe(raw: String, locale: Locale): DateTime = formatter.withLocale(locale).parseDateTime(raw)
    }
}
