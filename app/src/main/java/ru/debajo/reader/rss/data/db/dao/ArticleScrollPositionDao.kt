package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.debajo.reader.rss.data.db.model.DbArticle
import ru.debajo.reader.rss.data.db.model.DbArticleScrollPosition

@Dao
interface ArticleScrollPositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scroll: DbArticleScrollPosition)

    @Query("SELECT * FROM dbarticlescrollposition WHERE articleId=:articleId")
    suspend fun getScroll(articleId: String): DbArticleScrollPosition?

    @Query("DELETE FROM dbarticlescrollposition WHERE articleId=:articleId")
    suspend fun remove(articleId: String)

    // add sorting by persist timestamp
    @Query("SELECT * FROM dbarticle WHERE id in (SELECT articleId FROM dbarticlescrollposition) ORDER BY timestamp DESC")
    fun observeNotFullyReadArticles(): Flow<List<DbArticle>>
}
