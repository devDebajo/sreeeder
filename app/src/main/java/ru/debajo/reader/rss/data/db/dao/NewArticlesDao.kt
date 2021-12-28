package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.debajo.reader.rss.data.db.model.DbNewArticle

@Dao
interface NewArticlesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articles: List<DbNewArticle>)

    @Query("DELETE FROM dbnewarticle WHERE articleId IN (:articleIds)")
    suspend fun remove(articleIds: List<String>)

    @Query("DELETE FROM dbnewarticle WHERE channelUrl IN (:channelUrl)")
    suspend fun removeByChannelUrl(channelUrl: String)

    @Query("SELECT articleId FROM dbnewarticle")
    suspend fun getAllIds(): List<String>

    @Query("SELECT articleId FROM dbnewarticle WHERE channelUrl IN (SELECT articleId FROM dbchannelsubscription)")
    fun observeIds(): Flow<List<String>>
}
