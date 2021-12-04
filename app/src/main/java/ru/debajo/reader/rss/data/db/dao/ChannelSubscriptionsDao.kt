package ru.debajo.reader.rss.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.debajo.reader.rss.data.db.model.DbChannelSubscription

@Dao
interface ChannelSubscriptionsDao {
    @Query("SELECT * FROM dbchannelsubscription ORDER BY timestamp DESC")
    fun observeSubscriptions(): Flow<List<DbChannelSubscription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(subscription: DbChannelSubscription)

    @Query("DELETE FROM dbchannelsubscription WHERE url=:url")
    suspend fun remove(url: String)

    @Query("SELECT * FROM dbchannelsubscription WHERE url=:url")
    fun observeByUrl(url: String): Flow<List<DbChannelSubscription>>
}
