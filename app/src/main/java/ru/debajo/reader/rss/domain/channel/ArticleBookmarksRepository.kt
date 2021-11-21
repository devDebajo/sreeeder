package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.debajo.reader.rss.data.db.dao.ArticleBookmarksDao

class ArticleBookmarksRepository(
    private val articleBookmarksDao: ArticleBookmarksDao,
) {
    fun observe(): Flow<List<String>> {
       return articleBookmarksDao.getAll()
            .map { list -> list.map { it.articleId } }
            .flowOn(Dispatchers.IO)
    }
}
