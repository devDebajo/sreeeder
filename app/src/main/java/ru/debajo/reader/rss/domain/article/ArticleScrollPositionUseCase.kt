package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.debajo.reader.rss.data.converter.toDomainList
import ru.debajo.reader.rss.data.db.dao.ArticleScrollPositionDao
import ru.debajo.reader.rss.data.db.model.DbArticleScrollPosition
import ru.debajo.reader.rss.domain.model.DomainArticle

class ArticleScrollPositionUseCase(
    private val dao: ArticleScrollPositionDao,
) {
    suspend fun insert(articleId: String, scroll: Int) {
        dao.insert(DbArticleScrollPosition(articleId, scroll))
    }

    suspend fun getScroll(articleId: String): Float? {
        return dao.getScroll(articleId)?.scroll?.let { it / 100f }
    }

    suspend fun remove(articleId: String) {
        return dao.remove(articleId)
    }

    fun observeNotFullyReadArticles(): Flow<List<DomainArticle>> {
        return dao.observeNotFullyReadArticles()
            .map { it.toDomainList() }
    }
}
