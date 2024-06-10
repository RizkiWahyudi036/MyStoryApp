package com.example.storyapp.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapp.Api.ApiService
import com.example.storyapp.database.RemoteKeys
import com.example.storyapp.database.StoriesDatabase
import com.example.storyapp.preferences.SharedPreferences
import java.io.InvalidObjectException

@OptIn(ExperimentalPagingApi::class)
class StoriesRemote(private val database: StoriesDatabase, private val apiService: ApiService, context: Context): RemoteMediator<Int, Stories>(){

    private var pfef: SharedPreferences = SharedPreferences(context)

    override  suspend fun initialize(): InitializeAction{
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Stories>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                if (remoteKeys?.nextKey == null) {
                    throw InvalidObjectException("Remote key should not be null for $loadType")
                }
                remoteKeys.nextKey
            }
        }
        val token = "Bearer ${pfef.getUserToken()}"
        return tryFetchData(token, page, state, loadType)
    }

    private suspend fun tryFetchData(token: String, page: Int?, state: PagingState<Int, Stories>, loadType: LoadType): MediatorResult {
        val actualPage = page ?: INITIAL_PAGE_INDEX
        try {
            val responseData = apiService.getAllStoriesWithPaging(token, actualPage, state.config.pageSize).listStory
            val endOfPaginationReached = responseData.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH){
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storiesDAO().deleteAll()
                }
                val keys = responseData.map {
                    RemoteKeys(id = it.id, prevKey = if (actualPage == 1) null else actualPage - 1, nextKey = if(endOfPaginationReached) null else actualPage + 1)
                }
                database.remoteKeysDao().insertAll(keys)
                database.storiesDAO().insertStories(responseData)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (exception: Exception){
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Stories>): RemoteKeys?{
        return state.pages.lastOrNull() {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Stories>): RemoteKeys?{
        return state.pages.firstOrNull(){
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { data->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyClossetToCurrentPosition(state: PagingState<Int, Stories>): RemoteKeys?{
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }
}