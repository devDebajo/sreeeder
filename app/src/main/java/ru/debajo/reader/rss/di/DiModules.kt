package ru.debajo.reader.rss.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.prof.rssparser.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.reader.rss.data.db.RssDatabase
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.data.db.migrations.MIGRATION_1_2
import ru.debajo.reader.rss.data.db.migrations.MIGRATION_2_3
import ru.debajo.reader.rss.data.preferences.AppThemePreference
import ru.debajo.reader.rss.data.preferences.DynamicThemePreference
import ru.debajo.reader.rss.data.remote.load.ChannelsSearchRepository
import ru.debajo.reader.rss.data.remote.load.HtmlChannelUrlExtractor
import ru.debajo.reader.rss.data.remote.load.RssLoader
import ru.debajo.reader.rss.data.remote.service.FeedlyService
import ru.debajo.reader.rss.data.remote.service.ServiceFactory
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticlesRepository
import ru.debajo.reader.rss.domain.article.ViewedArticlesRepository
import ru.debajo.reader.rss.domain.cache.CacheManager
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.domain.feed.FeedListUseCase
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.domain.search.SearchChannelsUseCase
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.ui.add.AddChannelScreenViewModel
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channel.ChannelArticlesViewModel
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.host.HostViewModel
import ru.debajo.reader.rss.ui.main.MainViewModel
import ru.debajo.reader.rss.ui.settings.SettingsViewModel
import ru.debajo.reader.rss.ui.theme.AppThemeProvider

fun appModule(context: Context): Module = module {
    single { context.applicationContext }
    single { AppThemeProvider(get(), get(), get()) }
}

@SuppressLint("MissingPermission")
val MetricsModule = module {
    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseAnalytics.getInstance(get()) }
    single { Analytics(get()) }
}

val PreferencesModule = module {
    single { get<Context>().getSharedPreferences("sreeeder_prefs", Context.MODE_PRIVATE) }

    single { AppThemePreference(get()) }
    single { DynamicThemePreference(get()) }
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
    single { Gson() }
    single { ServiceFactory(get(), get()) }
    single<FeedlyService> { get<ServiceFactory>().createService("https://feedly.com") }
    single { ChannelsSearchRepository(get()) }
    single { HtmlChannelUrlExtractor(get()) }
}

val DbModule = module {
    single {
        Room.databaseBuilder(get(), RssDatabase::class.java, "sreeeder_db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }
    single { get<RssDatabase>(RssDatabase::class.java).favoriteChannelsDao() }
    single { get<RssDatabase>(RssDatabase::class.java).articlesDao() }
    single { get<RssDatabase>(RssDatabase::class.java).cacheMarkerDao() }
    single { get<RssDatabase>(RssDatabase::class.java).articleBookmarksDao() }
    single { get<RssDatabase>(RssDatabase::class.java).channelSubscriptionsDao() }
    single { get<RssDatabase>(RssDatabase::class.java).viewedArticlesDao() }
    single { CacheManager(get()) }
    single { RssLoadDbManager(get(), get(), get(), get(), get()) }
}

val RepositoryModule = module {
    single { ArticleBookmarksRepository(get(), get()) }
    single { ArticlesRepository(get()) }
    single { ChannelsRepository(get()) }
    single { ChannelsSubscriptionsRepository(get(), get()) }
    single { ViewedArticlesRepository(get()) }
}

val UseCaseModule = module {
    single { ChannelsSubscriptionsUseCase(get(), get()) }
    single { FeedListUseCase(get(), get()) }
    single { LoadArticlesUseCase(get(), get(), get()) }
    single { SearchChannelsUseCase(get(), get(), get()) }
}

val ViewModelModule = module {
    factory { ChannelsViewModel(get()) }
    factory { SettingsViewModel(get()) }
    factory { AddChannelScreenViewModel(get(), get()) }
    factory { ChannelArticlesViewModel(get(), get(), get(), get(), get()) }
    factory { FeedListViewModel(get(), get(), get(), get()) }
    factory { BookmarksListViewModel(get(), get(), get()) }
    factory { MainViewModel() }
    factory { HostViewModel(get()) }
}