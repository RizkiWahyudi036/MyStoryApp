package com.example.storyapp.preferences

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences (context: Context){
    val LOGIN = "login"
    val MY_PREF = "Main_pref"
    val MY_TOKEN_KEY = "Bearer"
    val sharedPreference: SharedPreferences

    init {
        sharedPreference = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)
    }

    fun setStatusLogin(status: Boolean){
        sharedPreference.edit().putBoolean(LOGIN, status).apply()
    }

    fun getStatusLogin(): Boolean{
        return sharedPreference.getBoolean(LOGIN,false)
    }

    fun saveUserToken(token: String){
        sharedPreference.edit().putString(MY_TOKEN_KEY,token).apply()
    }

    fun getUserToken(): String? {
        return sharedPreference.getString(MY_TOKEN_KEY," ")
    }

    fun clearUserToken(){
        sharedPreference.edit().remove(MY_TOKEN_KEY).apply()
    }

    fun clearUserLogin(){
        sharedPreference.edit().remove(LOGIN).apply()
    }
}