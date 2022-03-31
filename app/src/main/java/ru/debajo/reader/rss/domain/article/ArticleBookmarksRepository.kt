package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.db.dao.ArticleBookmarksDao
import ru.debajo.reader.rss.data.db.model.DbArticle
import ru.debajo.reader.rss.data.db.model.DbArticleBookmark
import ru.debajo.reader.rss.data.db.model.DbDateTime

class ArticleBookmarksRepository(
    private val articleBookmarksDao: ArticleBookmarksDao,
) {
    fun observeById(articleId: String): Flow<Boolean> {
        return articleBookmarksDao.observeCountById(articleId).map { it > 0 }
    }

    fun observeArticles(): Flow<List<DbArticle>> {
        return articleBookmarksDao.observeArticles()
    }

    fun observeIds(): Flow<List<String>> {
        return articleBookmarksDao.observeIds()
    }

    suspend fun toggle(articleId: String) {
        withContext(IO) {
            val saved = articleBookmarksDao.getById(articleId) != null
            if (saved) {
                articleBookmarksDao.remove(articleId)
            } else {
                articleBookmarksDao.add(DbArticleBookmark(articleId, DbDateTime.now()))
            }
        }
    }
}
