package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.debajo.reader.rss.data.db.model.DbArticle

@Dao
interface ArticlesDao {
    @Query("SELECT * FROM dbarticle WHERE channelUrl=:channelUrl")
    suspend fun getArticles(channelUrl: String): List<DbArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articles: List<DbArticle>)
}
