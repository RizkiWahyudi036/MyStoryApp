package com.example.storyapp.ui.addStory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.Injection
import com.example.storyapp.data.StoriesRepository
import com.example.storyapp.utils.MyResult
import androidx.lifecycle.LiveData
import com.example.storyapp.Api.AddNewStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val storiesRepository: StoriesRepository) : ViewModel() {

    fun addStories(token: String, description: RequestBody, img: MultipartBody.Part, lat: Float, lon: Float): LiveData<MyResult<AddNewStoryResponse>> =
        storiesRepository.addStories(token, description, img, lat, lon)

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddViewModel(Injection.provideRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    companion object {
        const val TAG = "AddViewModel"
    }
}