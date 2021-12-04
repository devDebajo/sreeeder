package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.debajo.reader.rss.data.db.model.DbChannel

@Dao
interface ChannelsDao {
    @Query("SELECT * FROM dbchannel")
    suspend fun getAll(): List<DbChannel>

    @Query("SELECT * FROM dbchannel WHERE url IN (:urls)")
    fun observeByUrls(urls: List<String>): Flow<List<DbChannel>>

    @Query("SELECT * FROM dbchannel WHERE url=:url")
    fun observeByUrl(url: String): Flow<List<DbChannel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(channel: DbChannel)

    @Query("DELETE FROM dbchannel WHERE url=:url")
    suspend fun remove(url: String)
}

