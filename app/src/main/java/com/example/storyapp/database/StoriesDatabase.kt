package com.example.storyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapp.data.Stories

@Database(entities = [Stories::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoriesDatabase : RoomDatabase() {

    abstract fun storiesDAO(): StoriesDAO
    abstract fun remoteKeysDao(): RemoteKeysDAO

    companion object {
        private var instance: StoriesDatabase? = null

        fun getDatabase(context: Context): StoriesDatabase {
            if (instance == null) {
                synchronized(StoriesDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            StoriesDatabase::class.java, "stories.db"
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return instance!!
        }
    }
}