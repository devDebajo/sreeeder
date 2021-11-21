package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.debajo.reader.rss.data.db.model.DbChannel

@Dao
interface ChannelsDao {
    @Query("SELECT * FROM dbchannel")
    suspend fun getAll(): List<DbChannel>

    @Query("SELECT * FROM dbchannel WHERE url IN (:urls)")
    suspend fun getByUrls(urls: List<String>): List<DbChannel>

    @Query("SELECT * FROM dbchannel WHERE url=:url")
    suspend fun getByUrl(url: String): DbChannel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(channel: DbChannel)

    @Query("DELETE FROM dbchannel WHERE url=:url")
    suspend fun remove(url: String)
}

