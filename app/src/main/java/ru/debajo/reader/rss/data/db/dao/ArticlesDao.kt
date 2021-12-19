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

    @Query("SELECT COUNT(*) FROM dbarticle WHERE id NOT IN (SELECT articleId FROM dbviewedarticle)")
    suspend fun getUnreadArticlesCount(): Long

    @Query("SELECT id FROM dbarticle WHERE id NOT IN (SELECT articleId FROM dbviewedarticle)")
    suspend fun getUnreadArticlesIds(): List<String>
}
