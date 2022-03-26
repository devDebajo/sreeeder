package ru.debajo.reader.rss.di

import android.app.NotificationManager
import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
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
import ru.debajo.reader.rss.data.db.migrations.MIGRATION_3_4
import ru.debajo.reader.rss.data.db.migrations.MIGRATION_4_5
import ru.debajo.reader.rss.data.dump.FileSaver
import ru.debajo.reader.rss.data.dump.OpmlDumper
import ru.debajo.reader.rss.data.preferences.*
import ru.debajo.reader.rss.data.preferences.base.PreferenceObserver
import ru.debajo.reader.rss.data.remote.ReadableArticleHelper
import ru.debajo.reader.rss.data.remote.load.ChannelsSearchRepository
import ru.debajo.reader.rss.data.remote.load.HtmlChannelUrlExtractor
import ru.debajo.reader.rss.data.remote.load.RssLoader
import ru.debajo.reader.rss.data.remote.service.FeedlyService
import ru.debajo.reader.rss.data.remote.service.ServiceFactory
import ru.debajo.reader.rss.data.updater.BackgroundUpdatesNotificationManager
import ru.debajo.reader.rss.data.updater.BackgroundUpdatesScheduler
import ru.debajo.reader.rss.data.updater.NotificationChannelCreator
import ru.debajo.reader.rss.data.updater.NotificationFactory
import ru.debajo.reader.rss.domain.article.*
import ru.debajo.reader.rss.domain.cache.CacheManager
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.domain.channel.SubscribeChannelsListUseCase
import ru.debajo.reader.rss.domain.feed.FeedListUseCase
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.domain.search.SearchChannelsUseCase
import ru.debajo.reader.rss.ui.add.AddChannelScreenViewModel
import ru.debajo.reader.rss.ui.article.UiArticleWebRenderViewModel
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channel.ChannelArticlesViewModel
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.feed.UiArticleNavigator
import ru.debajo.reader.rss.ui.host.HostViewModel
import ru.debajo.reader.rss.ui.main.MainViewModel
import ru.debajo.reader.rss.ui.settings.SettingsViewModel
import ru.debajo.reader.rss.ui.theme.AppThemeProvider

fun nonVariantModules(context: Context): List<Module> {
    return listOf(
        appModule(context),
        PreferencesModule,
        NetworkModule,
        DbModule,
        RepositoryModule,
        UseCaseModule,
        ViewModelModule,
        RefresherModule,
        NotificationModule,
    )
}

fun appModule(context: Context): Module = module {
    single { context.applicationContext }
    single { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    single { AppThemeProvider(get(), get()) }
    single { UiArticleNavigator(get()) }
}

val PreferencesModule = module {
    single { get<Context>().getSharedPreferences("sreeeder_prefs", Context.MODE_PRIVATE) }

    single { PreferenceObserver() }
    single { AppThemePreference(get()) }
    single { DynamicThemePreference(get()) }
    single { BackgroundUpdatesEnabledPreference(get()) }
    single { MetricsEnabledPreference(get()) }
    single { UseEmbeddedWebPageRenderPreference(get()) }
    single { ShowNavigationTitlesPreference(get()) }
    single { CrashlyticsPreference(get()) }
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
    single { ReadableArticleHelper(get()) }
}

val DbModule = module {
    single {
        Room.databaseBuilder(get(), RssDatabase::class.java, "sreeeder_db")
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
            )
            .build()
    }
    single { get<RssDatabase>(RssDatabase::class.java).favoriteChannelsDao() }
    single { get<RssDatabase>(RssDatabase::class.java).articlesDao() }
    single { get<RssDatabase>(RssDatabase::class.java).cacheMarkerDao() }
    single { get<RssDatabase>(RssDatabase::class.java).articleBookmarksDao() }
    single { get<RssDatabase>(RssDatabase::class.java).channelSubscriptionsDao() }
    single { get<RssDatabase>(RssDatabase::class.java).newArticlesDao() }
    single { CacheManager(get()) }
    single { RssLoadDbManager(get(), get(), get(), get(), get(), get(), get()) }
    single { OpmlDumper(get()) }
    single { FileSaver(get()) }
}

val RepositoryModule = module {
    single { ArticleBookmarksRepository(get()) }
    single { ArticlesRepository(get()) }
    single { ChannelsRepository(get()) }
    single { ChannelsSubscriptionsRepository(get()) }
    single { NewArticlesRepository(get()) }
}

val UseCaseModule = module {
    single { ChannelsSubscriptionsUseCase(get(), get()) }
    single { FeedListUseCase(get(), get()) }
    single { LoadArticlesUseCase(get(), get(), get()) }
    single { SearchChannelsUseCase(get(), get(), get()) }
    single { NewArticlesUseCase(get(), get()) }
    single { ClearArticlesUseCase(get(), get(), get()) }
    single { SubscribeChannelsListUseCase(get(), get()) }
}

val ViewModelModule = module {
    factory { ChannelsViewModel(get()) }
    factory { SettingsViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { AddChannelScreenViewModel(get()) }
    factory { ChannelArticlesViewModel(get(), get(), get(), get()) }
    factory { FeedListViewModel(get(), get(), get(), get(), get()) }
    factory { BookmarksListViewModel(get(), get()) }
    factory { MainViewModel(get(), get(), get()) }
    factory { HostViewModel(get()) }
    factory { UiArticleWebRenderViewModel(get(), get()) }
}

val RefresherModule = module {
    single { WorkManager.getInstance(get()) }
    single { BackgroundUpdatesScheduler(get(), get()) }
}

val NotificationModule = module {
    single { BackgroundUpdatesNotificationManager(get(), get(), get(), get()) }
    single { NotificationChannelCreator(get(), get()) }
    single { NotificationFactory(get()) }
}
