package ru.debajo.reader.rss.data.converter

import android.os.Build
import org.joda.time.DateTimeZone
import ru.debajo.reader.rss.BuildConfig
import ru.debajo.reader.rss.data.db.model.DbDateTime
import ru.debajo.reader.rss.data.db.model.DbError
import ru.debajo.reader.rss.data.remote.model.RemoteError
import java.util.*

fun Throwable.toDb(
    fatal: Boolean,
    customMessage: String?,
    tag: String?
): DbError {
    val stackTrace = stackTraceToString()
    return DbError(
        id = UUID.randomUUID().toString(),
        stackTrace = stackTrace,
        message = message,
        customMessage = customMessage.takeIf { it != stackTrace },
        tag = tag,
        date = DbDateTime.now(),
        appVersion = BuildConfig.VERSION_NAME,
        sdkVersion = Build.VERSION.SDK_INT,
        fatal = fatal,
    )
}

fun DbError.toRemote(): RemoteError {
    return RemoteError(
        id = id,
        stackTrace = stackTrace,
        message = message,
        customMessage = customMessage,
        tag = tag,
        dateUtc = date.dateTime.withZone(DateTimeZone.UTC).millis,
        appVersion = appVersion,
        sdkVersion = sdkVersion,
        fatal = fatal,
    )
}
