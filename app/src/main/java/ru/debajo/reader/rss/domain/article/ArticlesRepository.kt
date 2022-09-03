package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.debajo.reader.rss.data.converter.toDomainList
import ru.debajo.reader.rss.data.db.dao.ArticlesDao
import ru.debajo.reader.rss.data.db.model.DbArticle
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannelUrl

class ArticlesRepository(private val articlesDao: ArticlesDao) {

    fun getArticles(channelUrl: DomainChannelUrl): Flow<List<DomainArticle>> {
        return articlesDao.observeChannelArticles(channelUrl.url)
            .map { articles -> articles.toDomainList() }
    }

    suspend fun removeFromDb(channelUrl: DomainChannelUrl) {
        articlesDao.removeByChannelUrl(channelUrl.url)
    }

    suspend fun contains(articlesId: String): Boolean {
        return articlesDao.getAllByIds(listOf(articlesId)).isNotEmpty()
    }

    suspend fun persist(article: DbArticle) {
        articlesDao.insert(article)
    }
}
