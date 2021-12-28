package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.db.dao.NewArticlesDao
import ru.debajo.reader.rss.data.db.model.DbNewArticle
import ru.debajo.reader.rss.domain.model.DomainChannelUrl

class NewArticlesRepository(
    private val newArticlesDao: NewArticlesDao,
) {
    suspend fun saveNewArticlesIds(channelUrl: DomainChannelUrl, ids: List<String>) {
        withContext(IO) { newArticlesDao.insert(ids.map { DbNewArticle(it, channelUrl.url) }) }
    }

    suspend fun onViewed(articleIds: List<String>) {
        withContext(IO) { newArticlesDao.remove(articleIds) }
    }

    suspend fun getNewArticlesIds(): Set<String> {
        return withContext(IO) { newArticlesDao.getAllIds().toSet() }
    }

    suspend fun remove(channelUrl: DomainChannelUrl) {
        withContext(IO) { newArticlesDao.removeByChannelUrl(channelUrl.url) }
    }

    fun observeIds(): Flow<Set<String>> = newArticlesDao.observeIds().map { it.toSet() }
}
