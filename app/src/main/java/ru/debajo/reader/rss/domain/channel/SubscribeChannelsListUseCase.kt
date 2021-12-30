package ru.debajo.reader.rss.domain.channel

import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.data.db.await
import ru.debajo.reader.rss.domain.model.DomainChannelUrl

class SubscribeChannelsListUseCase(
    private val rssLoadDbManager: RssLoadDbManager,
    private val channelsSubscriptionsRepository: ChannelsSubscriptionsRepository,
) {
    suspend fun subscribe(urls: List<DomainChannelUrl>) {
        for (url in urls) {
            if (rssLoadDbManager.refreshChannel(url, false).await() != null) {
                channelsSubscriptionsRepository.subscribeIfNeed(url)
            }
        }
    }
}
