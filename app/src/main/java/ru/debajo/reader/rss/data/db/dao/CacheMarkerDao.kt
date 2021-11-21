package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.debajo.reader.rss.data.db.model.DbCacheMarker

@Dao
interface CacheMarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marker: DbCacheMarker)

    @Query("SELECT * FROM dbcachemarker WHERE cacheGroup=:group AND dataId=:dataId")
    suspend fun get(group: String, dataId: String): DbCacheMarker?

    @Query("SELECT * FROM dbcachemarker WHERE cacheGroup=:group AND dataId in (:dataIds)")
    suspend fun get(group: String, dataIds: List<String>): List<DbCacheMarker>
}
