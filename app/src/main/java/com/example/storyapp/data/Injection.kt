package com.example.storyapp.data

import android.content.Context
import com.example.storyapp.Api.ApiConfig
import com.example.storyapp.database.StoriesDatabase

object Injection {
    fun provideRepository(context: Context): StoriesRepository {
        val database = StoriesDatabase.getDatabase(context)
        val apiService = ApiConfig().getApiService()
        return StoriesRepository(database, apiService, context)
    }
}
