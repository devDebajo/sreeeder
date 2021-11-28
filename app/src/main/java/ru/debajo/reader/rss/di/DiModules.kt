package ru.debajo.reader.rss.di

import android.content.Context
import androidx.room.Room
import com.prof.rssparser.Parser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.reader.rss.data.db.RssDatabase
import ru.debajo.reader.rss.data.remote.RssLoader
import ru.debajo.reader.rss.domain.cache.CacheManager
import ru.debajo.reader.rss.domain.channel.*
import ru.debajo.reader.rss.ui.add.AddChannelScreenViewModel
import ru.debajo.reader.rss.ui.article.ArticleDetailsViewModel
import ru.debajo.reader.rss.ui.channel.ChannelArticlesViewModel
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.settings.SettingsViewModel
import ru.debajo.reader.rss.ui.theme.AppThemeProvider
import java.nio.charset.Charset

fun appModule(context: Context): Module = module {
    single { context.applicationContext }
    single { get<Context>().getSharedPreferences("sreeeder_prefs", Context.MODE_PRIVATE) }
    single { AppThemeProvider(get()) }
}

val NetworkModule = module {
    single { RssLoader(get()) }
    single {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }
    single {
        Parser.Builder()
            .okHttpClient(get())
            .context(get())
            .charset(Charset.forName("UTF-8"))
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

@ExperimentalCoroutinesApi
val UseCaseModule = module {
    single { ArticleBookmarksUseCase(get(), get()) }
    single { ChannelsSubscriptionsUseCase(get(), get()) }
}

@ExperimentalCoroutinesApi
val ViewModelModule = module {
    factory { ChannelsViewModel(get()) }
    factory { SettingsViewModel(get()) }
    factory { AddChannelScreenViewModel(get()) }
    factory { ChannelArticlesViewModel(get()) }
    factory { ArticleDetailsViewModel() }
}