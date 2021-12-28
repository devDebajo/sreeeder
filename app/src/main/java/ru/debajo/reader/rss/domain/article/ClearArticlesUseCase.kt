package ru.debajo.reader.rss.domain.article

import ru.debajo.reader.rss.data.converter.toDomain
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.ui.channels.model.UiChannelUrl

class ClearArticlesUseCase(
    private val newArticlesRepository: NewArticlesRepository,
    private val articlesRepository: ArticlesRepository,
    private val channelsRepository: ChannelsRepository,
) {

    suspend fun clear(channelUrl: UiChannelUrl) {
        newArticlesRepository.remove(channelUrl.toDomain())
        articlesRepository.removeFromDb(channelUrl.toDomain())
        channelsRepository.removeFromDb(channelUrl.toDomain())
    }
}
