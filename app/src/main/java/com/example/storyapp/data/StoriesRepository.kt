package com.example.storyapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.Api.*
import com.example.storyapp.database.StoriesDatabase
import com.example.storyapp.utils.MyResult
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoriesRepository(private val storiesDatabase: StoriesDatabase, private val apiService: ApiService, private val context: Context) {

    val myResultUserLogin = MediatorLiveData<MyResult<LoginResponse>>()
    val myResultUserRegister = MediatorLiveData<MyResult<SignupResponse>>()
    val myResultAddStories = MediatorLiveData<MyResult<AddNewStoryResponse>>()
    val myResultMapsStories = MediatorLiveData<MyResult<GetAllStoryResponse>>()

    fun getStories(): LiveData<PagingData<Stories>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoriesRemote(storiesDatabase, apiService, context),
            pagingSourceFactory = {
                storiesDatabase.storiesDAO().getAllStories()
            }
        ).liveData
    }

    fun login(email: String, password: String): LiveData<MyResult<LoginResponse>> {
        return performCall(apiService.login(email, password), myResultUserLogin)
    }

    fun register(name: String, email: String, password: String): LiveData<MyResult<SignupResponse>> {
        return performCall(apiService.register(name, email, password), myResultUserRegister)
    }

    fun addStories(token: String, description: okhttp3.RequestBody, img: MultipartBody.Part, lat: Float, lon: Float):
            LiveData<MyResult<AddNewStoryResponse>> {
        return performCall(apiService.uploadStoriesWithLoc(token, img, description, lat, lon), myResultAddStories)
    }

    fun getStoriesWithLocation(token: String): LiveData<MyResult<GetAllStoryResponse>> {
        return performCall(apiService.getAllStoriesWithLocation(token), myResultMapsStories)
    }

    private fun <T> performCall(call: Call<T>, mediatorLiveData: MediatorLiveData<MyResult<T>>): LiveData<MyResult<T>> {
        mediatorLiveData.value = MyResult.Loading
        val liveData = MutableLiveData<T>()
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    liveData.value = response.body()
                } else {
                    mediatorLiveData.value = MyResult.Error(response.message())
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                mediatorLiveData.value = MyResult.Error(t.message.toString())
            }
        })
        mediatorLiveData.addSource(liveData) { data ->
            mediatorLiveData.value = MyResult.Success(data)
        }
        return mediatorLiveData
    }
}
