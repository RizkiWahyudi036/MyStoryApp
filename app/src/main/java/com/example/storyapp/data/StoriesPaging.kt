package com.example.storyapp.data

import android.content.Context
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.Api.ApiService
import com.example.storyapp.preferences.SharedPreferences

class StoriesPaging(private val apiService: ApiService, private val context: Context) : PagingSource<Int, Stories>() {

    companion object {
        fun snapshot(items: List<Stories>): PagingData<Stories> {
            return PagingData.from(items)
        }
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stories> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = "Bearer ${SharedPreferences(context).getUserToken()}"
            val responseData = apiService.getAllStoriesWithPaging(token, position, params.loadSize).listStory
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Stories>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}