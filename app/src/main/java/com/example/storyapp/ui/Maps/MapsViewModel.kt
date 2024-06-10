package com.example.storyapp.ui.Maps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.Injection
import com.example.storyapp.data.StoriesRepository

class MapsViewModel(private val storiesRepository: StoriesRepository): ViewModel() {

    fun getStoriesWithLocation(token: String) = storiesRepository.getStoriesWithLocation(token)

    class Factory(private var context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MapsViewModel(Injection.provideRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}