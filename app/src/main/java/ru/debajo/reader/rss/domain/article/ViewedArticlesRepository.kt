package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.db.dao.ViewedArticlesDao
import ru.debajo.reader.rss.data.db.model.DbViewedArticle

class ViewedArticlesRepository(
    private val viewedArticlesDao: ViewedArticlesDao
) {
    private val viewedCache: HashSet<String> = HashSet()

    suspend fun onViewed(articleId: String) {
        if (synchronized(viewedCache) { viewedCache.contains(articleId) }) {
            return
        }

        withContext(IO) {
            viewedArticlesDao.insert(DbViewedArticle(articleId))
        }

        synchronized(viewedCache) {
            viewedCache.add(articleId)
        }
    }

    suspend fun getViewedArticlesIds(articleIds: List<String>): Set<String> {
        val viewedArticles = withContext(IO) {
            viewedArticlesDao.getByIds(articleIds).map { it.articleId }.toSet()
        }

        synchronized(viewedCache) {
            viewedCache.addAll(viewedArticles)
        }

        return viewedArticles
    }
}
