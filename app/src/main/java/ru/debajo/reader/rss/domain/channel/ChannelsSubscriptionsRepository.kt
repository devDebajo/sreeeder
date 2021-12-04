package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.db.dao.ChannelSubscriptionsDao
import ru.debajo.reader.rss.data.db.model.DbChannelSubscription
import ru.debajo.reader.rss.data.db.model.DbDateTime
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import timber.log.Timber

class ChannelsSubscriptionsRepository(
    private val dao: ChannelSubscriptionsDao
) {
    suspend fun toggle(url: DomainChannelUrl) {
        withContext(IO) {
            val isSubscribed = isSubscribed(url).first()
            if (isSubscribed) {
                dao.remove(url.url)
            } else {
                dao.add(DbChannelSubscription(url.url, DbDateTime.now()))
            }
        }
    }

    fun isSubscribed(url: DomainChannelUrl): Flow<Boolean> {
        return dao.observeByUrl(url.url).map { it.isNotEmpty() }
    }

    fun observe(): Flow<List<DomainChannelUrl>> {
        return dao.observeSubscriptions()
            .map { list ->
                val urls = list.map { DomainChannelUrl(it.url) }
                urls.forEach { Timber.tag("Subscription").d(it.url) }
                urls
            }
            .flowOn(IO)
    }
}