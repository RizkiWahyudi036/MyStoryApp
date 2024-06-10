package com.example.storyapp.utils

sealed class MyResult <out R> private constructor(){
    data class Success<out T>(val list: T): MyResult<T>()
    data class Error(val Error: String): MyResult<Nothing>()
    object Loading: MyResult<Nothing>()
}