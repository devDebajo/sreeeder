package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import ru.debajo.reader.rss.domain.model.DomainChannel

class ChannelsSubscriptionsUseCase(
    private val channelsRepository: ChannelsRepository,
    private val channelsSubscriptionsRepository: ChannelsSubscriptionsRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(): Flow<List<DomainChannel>> {
        return channelsSubscriptionsRepository.observe()
            .flatMapLatest { urls -> channelsRepository.getChannelsByUrls(urls) }
            .distinctUntilChanged()
    }
}
