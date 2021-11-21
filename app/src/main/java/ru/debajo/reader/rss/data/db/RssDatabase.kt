package ru.debajo.reader.rss.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.debajo.reader.rss.data.db.converter.DbDateTimeConverter
import ru.debajo.reader.rss.data.db.dao.*
import ru.debajo.reader.rss.data.db.model.*

@Database(
    entities = [
        DbArticle::class,
        DbChannel::class,
        DbArticleBookmark::class,
        DbChannelSubscription::class,
        DbCacheMarker::class,
    ],
    version = 1,
)
@TypeConverters(DbDateTimeConverter::class)
abstract class RssDatabase : RoomDatabase() {
    abstract fun favoriteChannelsDao(): ChannelsDao

    abstract fun articleBookmarksDao(): ArticleBookmarksDao

    abstract fun articlesDao(): ArticlesDao

    abstract fun cacheMarkerDao(): CacheMarkerDao

    abstract fun channelSubscriptionsDao(): ChannelSubscriptionsDao
}
