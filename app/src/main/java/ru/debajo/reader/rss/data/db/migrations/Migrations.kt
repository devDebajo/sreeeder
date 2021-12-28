package ru.debajo.reader.rss.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `dbarticle` ADD COLUMN categories TEXT NOT NULL DEFAULT '[]'")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `dbviewedarticle` (`articleId` TEXT NOT NULL, PRIMARY KEY(`articleId`))")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE `dbviewedarticle`")
        database.execSQL("CREATE TABLE IF NOT EXISTS `dbnewarticle` (`articleId` TEXT NOT NULL, `channelUrl` TEXT NOT NULL, PRIMARY KEY(`articleId`))")
    }
}
