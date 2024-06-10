package com.example.storyapp.ui.Login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.Injection
import com.example.storyapp.data.StoriesRepository
import com.example.storyapp.preferences.SharedPreferences

class LoginViewModel(private val storiesRepository: StoriesRepository,
                     private val context: Context): ViewModel(){

    private val sharedPreferences = SharedPreferences(context)

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getStatusLogin()
    }

    fun saveUserToken(token: String) {
        sharedPreferences.saveUserToken(token)
    }

    fun setUserLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.setStatusLogin(isLoggedIn)
    }

    fun getAllUserLogin(email: String, password: String) =
        storiesRepository.login(email,password)

    class Factory(private var context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(Injection.provideRepository(context), context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}