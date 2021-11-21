package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.flow.map

class ArticleBookmarksUseCase(
    private val channelsRepository: ChannelsRepository,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
) {
    fun observe() {
        articleBookmarksRepository.observe()
            .map { articleIds ->
                //channelsRepository.
            }
    }
}