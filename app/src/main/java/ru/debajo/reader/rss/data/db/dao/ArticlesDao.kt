package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.debajo.reader.rss.data.db.model.DbArticle

@Dao
interface ArticlesDao {
    @Query("SELECT * FROM dbarticle WHERE channelUrl=:channelUrl ORDER BY timestamp DESC")
    fun observeChannelArticles(channelUrl: String): Flow<List<DbArticle>>

    @Query("SELECT * FROM dbarticle WHERE id in (:ids) ORDER BY timestamp DESC")
    fun observeArticles(ids: List<String>): Flow<List<DbArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articles: List<DbArticle>)

    @Query("DELETE FROM dbarticle WHERE channelUrl=:channelUrl AND id NOT IN (SELECT articleId FROM dbarticlebookmark)")
    suspend fun removeByChannelUrl(channelUrl: String)

    @Query("SELECT id FROM dbarticle WHERE id in (:ids)")
    suspend fun getAllByIds(ids: List<String>): List<String>
}

suspend fun ArticlesDao.getNonExistIds(articleIds: List<String>): List<String> {
    val idsInDb = getAllByIds(articleIds).toSet()
    return articleIds.filter { it !in idsInDb }
}
