package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.debajo.reader.rss.data.db.model.DbError

@Dao
interface ErrorsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(error: DbError)

    @Query("SELECT * FROM dberror")
    suspend fun getAll(): List<DbError>

    @Query("DELETE FROM dberror")
    suspend fun deleteAll()
}
