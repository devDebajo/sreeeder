package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.debajo.reader.rss.data.db.dao.ChannelSubscriptionsDao
import ru.debajo.reader.rss.data.db.model.DbChannelSubscription
import ru.debajo.reader.rss.data.db.model.DbDateTime

class ChannelsSubscriptionsRepository(
    private val dao: ChannelSubscriptionsDao
) {
    suspend fun add(url: String) {
        dao.add(DbChannelSubscription(url, DbDateTime.now()))
    }

    suspend fun remove(url: String) {
        dao.remove(url)
    }

    suspend fun isSubscribed(url: String): Boolean {
        return dao.getByUrl(url) != null
    }

    fun observe(): Flow<List<String>> {
        return dao.observeSubscriptions()
            .map { list -> list.map { it.url } }
            .flowOn(IO)
    }
}