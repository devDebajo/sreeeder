package ru.debajo.reader.rss.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATIONS = arrayOf(
    object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `dbarticle` ADD COLUMN categories TEXT NOT NULL DEFAULT '[]'")
        }
    },

    object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `dbviewedarticle` (`articleId` TEXT NOT NULL, PRIMARY KEY(`articleId`))")
        }
    },

    object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE `dbviewedarticle`")
            database.execSQL("CREATE TABLE IF NOT EXISTS `dbnewarticle` (`articleId` TEXT NOT NULL, `channelUrl` TEXT NOT NULL, PRIMARY KEY(`articleId`))")
        }
    },

    object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `dbchannel` ADD COLUMN imageDominantColor INTEGER DEFAULT null")
        }
    },

    object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `DbArticleScrollPosition` (`articleId` TEXT NOT NULL, `scroll` INTEGER NOT NULL, PRIMARY KEY(`articleId`))");
        }
    },

    object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `dbarticle` ADD COLUMN `channelName` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `dbarticle` ADD COLUMN `channelImage` TEXT DEFAULT null")
        }
    }
)
