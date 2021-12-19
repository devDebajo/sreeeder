package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.db.dao.ArticlesDao
import ru.debajo.reader.rss.data.db.dao.ViewedArticlesDao
import ru.debajo.reader.rss.data.db.model.DbViewedArticle

class ViewedArticlesRepository(
    private val viewedArticlesDao: ViewedArticlesDao,
    private val articlesDao: ArticlesDao
) {
    suspend fun onViewed() {
        withContext(IO) {
            viewedArticlesDao.insert(articlesDao.getUnreadArticlesIds().map { DbViewedArticle(it) })
        }
    }

    suspend fun getViewedArticlesIds(articleIds: List<String>): Set<String> {
        return withContext(IO) {
            viewedArticlesDao.getByIds(articleIds).map { it.articleId }.toSet()
        }
    }
}
