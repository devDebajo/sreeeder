package ru.debajo.reader.rss.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.prof.rssparser.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.reader.rss.data.db.RssDatabase
import ru.debajo.reader.rss.data.remote.RssLoadDbManager
import ru.debajo.reader.rss.data.remote.RssLoader
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticlesRepository
import ru.debajo.reader.rss.domain.cache.CacheManager
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.domain.feed.FeedListUseCase
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.ui.add.AddChannelScreenViewModel
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channel.ChannelArticlesViewModel
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.main.MainViewModel
import ru.debajo.reader.rss.ui.settings.SettingsViewModel
import ru.debajo.reader.rss.ui.theme.AppThemeProvider

fun appModule(context: Context): Module = module {
    single { context.applicationContext }
    single { get<Context>().getSharedPreferences("sreeeder_prefs", Context.MODE_PRIVATE) }
    single { AppThemeProvider(get(), get()) }
}

@SuppressLint("MissingPermission")
val MetricsModule = module {
    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseAnalytics.getInstance(get()) }
    single { Analytics(get()) }
}

val NetworkModule = module {
    single { RssLoader(get(), get()) }
    single {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BASIC
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
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
    single { RssLoadDbManager(get(), get(), get(), get(), get()) }
}

val RepositoryModule = module {
    single { ArticleBookmarksRepository(get(), get()) }
    single { ArticlesRepository(get()) }
    single { ChannelsRepository(get()) }
    single { ChannelsSubscriptionsRepository(get(), get()) }
}

val UseCaseModule = module {
    single { ChannelsSubscriptionsUseCase(get(), get()) }
    single { FeedListUseCase(get(), get()) }
    single { LoadArticlesUseCase(get(), get(), get()) }
}

val ViewModelModule = module {
    factory { ChannelsViewModel(get()) }
    factory { SettingsViewModel(get()) }
    factory { AddChannelScreenViewModel(get(), get(), get()) }
    factory { ChannelArticlesViewModel(get(), get(), get(), get()) }
    factory { FeedListViewModel(get(), get(), get()) }
    factory { BookmarksListViewModel(get(), get()) }
    factory { MainViewModel() }
}