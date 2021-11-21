package ru.debajo.reader.rss.ext

inline fun <T> Result<T>.switchIfEmpty(block: () -> T): Result<T> {
    return if (isFailure) {
        runCatching { block() }
    } else {
        this
    }
}

inline fun <T> Result<T>.filter(predicate: (T) -> Boolean): Result<T> {
    return if (isSuccess) {
        if (predicate(getOrThrow())) {
            this
        } else {
            Result.failure(IllegalStateException())
        }
    } else {
        this
    }
}
