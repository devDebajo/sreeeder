package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.debajo.reader.rss.data.converter.toDomain
import ru.debajo.reader.rss.data.converter.toDomainList
import ru.debajo.reader.rss.data.converter.unwrap
import ru.debajo.reader.rss.data.db.dao.ChannelsDao
import ru.debajo.reader.rss.domain.model.DomainChannel
import ru.debajo.reader.rss.domain.model.DomainChannelUrl

class ChannelsRepository(
    private val channelsDao: ChannelsDao,
) {

    fun getChannelsByUrls(urls: List<DomainChannelUrl>): Flow<List<DomainChannel>> {
        val urlsIndices = urls.withIndex().associateBy({ it.value }, { it.index })
        return channelsDao.observeByUrls(urls.unwrap())
            .map { channels -> channels.toDomainList().sortedBy { urlsIndices[it.url] ?: urls.size } }
    }

    fun getChannel(url: DomainChannelUrl): Flow<DomainChannel?> {
        return channelsDao.observeByUrl(url.url)
            .map { channels -> channels.firstOrNull()?.toDomain() }
    }

    suspend fun removeFromDb(url: DomainChannelUrl) {
        channelsDao.remove(url.url)
    }
}
