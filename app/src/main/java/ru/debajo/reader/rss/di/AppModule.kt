package ru.debajo.reader.rss.di

import android.content.Context
import androidx.room.Room
import com.prof.rssparser.Parser
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.reader.rss.data.db.RssDatabase
import ru.debajo.reader.rss.data.remote.RssLoader
import ru.debajo.reader.rss.domain.cache.CacheManager
import ru.debajo.reader.rss.domain.channel.*
import java.nio.charset.Charset

fun appModule(context: Context): Module = module {
    single { context.applicationContext }
}

val NetworkModule = module {
    single { RssLoader(get()) }
    single {
        Parser.Builder()
            .context(get())
            .charset(Charset.forName("ISO-8859-7"))
            .build()
    }
}

val DbModule = module {
    single { Room.databaseBuilder(get(), RssDatabase::class.java, "sreeeder_db").build() }
    single { get<RssDatabase>(RssDatabase::class.java).favoriteChannelsDao() }
    single { get<RssDatabase>(RssDatabase::class.java).articlesDao() }
    single { get<RssDatabase>(RssDatabase::class.java).cacheMarkerDao() }
    single { get<RssDatabase>(RssDatabase::class.java).articleBookmarksDao() }
    single { get<RssDatabase>(RssDatabase::class.java).channelSubscriptionsDao() }
    single { CacheManager(get()) }
}

val RepositoryModule = module {
    single { ArticleBookmarksRepository(get()) }
    single { ChannelsRepository(get(), get(), get()) }
    single { ChannelsSubscriptionsRepository(get()) }
}

val UseCaseModule = module {
    single { ArticleBookmarksUseCase(get(), get()) }
    single { ChannelsSubscriptionsUseCase(get(), get()) }
}