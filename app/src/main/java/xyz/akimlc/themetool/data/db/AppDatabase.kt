package xyz.akimlc.themetool.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DownloadEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
}