package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.debajo.reader.rss.data.db.model.DbArticle
import ru.debajo.reader.rss.data.db.model.DbArticleBookmark

@Dao
interface ArticleBookmarksDao {
    @Query("SELECT articleId FROM dbarticlebookmark ORDER BY timestamp DESC")
    fun observeIds(): Flow<List<String>>

    // add sorting by persist timestamp
    @Query("SELECT * FROM dbarticle WHERE id in (SELECT articleId FROM dbarticlebookmark)")
    fun observeArticles(): Flow<List<DbArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(bookmark: DbArticleBookmark)

    @Query("DELETE FROM dbarticlebookmark WHERE articleId=:articleId")
    suspend fun remove(articleId: String)

    @Query("SELECT * FROM dbarticlebookmark WHERE articleId=:articleId")
    suspend fun getById(articleId: String): DbArticleBookmark?

    @Query("SELECT count(*) FROM dbarticlebookmark WHERE articleId=:articleId")
    fun observeCountById(articleId: String): Flow<Int>
}
