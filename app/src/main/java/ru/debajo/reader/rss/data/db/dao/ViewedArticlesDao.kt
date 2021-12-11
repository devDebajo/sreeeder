package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.debajo.reader.rss.data.db.model.DbViewedArticle

@Dao
interface ViewedArticlesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: DbViewedArticle)

    @Query("SELECT * FROM dbviewedarticle WHERE articleId IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<DbViewedArticle>
}