package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    fun observe(): Flow<List<String>> {
        return flowOf(
            listOf(
                "https://blog.jetbrains.com/feed",
                "https://news.un.org/feed/subscribe/ru/audio-product/all/audio-rss.xml",
                "https://www.nasa.gov/rss/dyn/breaking_news.rss",
                "https://www.nasa.gov/rss/dyn/educationnews.rss",
                "https://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss",
                "https://www.nasa.gov/rss/dyn/mission_pages/kepler/news/kepler-newsandfeatures-RSS.rss",
            )
        )
        return dao.observeSubscriptions()
            .map { list -> list.map { it.url } }
            .flowOn(IO)
    }
}