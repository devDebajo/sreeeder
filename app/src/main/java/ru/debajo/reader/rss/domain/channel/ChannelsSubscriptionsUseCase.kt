package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import ru.debajo.reader.rss.domain.model.DomainChannel

@ExperimentalCoroutinesApi
class ChannelsSubscriptionsUseCase(
    private val channelsRepository: ChannelsRepository,
    private val channelsSubscriptionsRepository: ChannelsSubscriptionsRepository,
) {
    fun observe(): Flow<List<DomainChannel>> {
        return channelsSubscriptionsRepository.observe()
            .flatMapLatest { urls ->
                channelsRepository.getChannelsByUrls(urls)
            }
    }
}
