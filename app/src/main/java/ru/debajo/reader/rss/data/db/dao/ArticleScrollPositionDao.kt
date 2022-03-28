package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.debajo.reader.rss.data.db.model.DbArticleScrollPosition

@Dao
interface ArticleScrollPositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scroll: DbArticleScrollPosition)

    @Query("SELECT * FROM dbarticlescrollposition WHERE articleId=:id")
    suspend fun getScroll(id: String): DbArticleScrollPosition?
}
