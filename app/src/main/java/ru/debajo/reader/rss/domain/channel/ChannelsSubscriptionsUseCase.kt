package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.debajo.reader.rss.domain.model.DomainChannel

class ChannelsSubscriptionsUseCase(
    private val channelsRepository: ChannelsRepository,
    private val channelsSubscriptionsRepository: ChannelsSubscriptionsRepository,
) {
    fun observe(): Flow<List<DomainChannel>> {
        return channelsSubscriptionsRepository.observe()
            .map { urls -> channelsRepository.getChannelsByUrls(urls) }
    }
}
