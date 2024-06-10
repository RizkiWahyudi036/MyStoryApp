package com.example.storyapp.ui.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.Injection
import com.example.storyapp.data.StoriesRepository

class RegisterViewModel (private val storiesRepository: StoriesRepository): ViewModel(){

    fun getAllUserRegister(name: String, email: String, password: String) =
        storiesRepository.register(name, email, password)

    class Factory(private var context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(Injection.provideRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}