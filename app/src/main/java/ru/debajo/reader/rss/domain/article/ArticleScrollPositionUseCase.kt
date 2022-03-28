package ru.debajo.reader.rss.domain.article

import ru.debajo.reader.rss.data.db.dao.ArticleScrollPositionDao
import ru.debajo.reader.rss.data.db.model.DbArticleScrollPosition

class ArticleScrollPositionUseCase(
    private val dao: ArticleScrollPositionDao,
) {
    suspend fun insert(articleId: String, scroll: Int) {
        dao.insert(DbArticleScrollPosition(articleId, scroll))
    }

    suspend fun getScroll(articleId: String): Int? {
        return dao.getScroll(articleId)?.scroll
    }
}
