package com.example.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.adapter.StoriesAdapter
import com.example.storyapp.data.Stories
import com.example.storyapp.data.StoriesPaging
import com.example.storyapp.data.StoriesRepository
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.MainDispatcherRule
import com.example.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository

    @Test
    fun `when Get allStories Should Not Null and Return Data`() = runBlockingTest {
        val dummyStories = DataDummy.generateDummyAllStories()
        val data: PagingData<Stories> = StoriesPaging.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<Stories>>()
        expectedStories.value = data
        Mockito.`when`(storiesRepository.getStories()).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(storiesRepository)
        val actualStories: PagingData<Stories> = mainViewModel.allStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get allStories Empty Should Return No Data`() = runBlockingTest {
        val data: PagingData<Stories> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<Stories>>()
        expectedStories.value = data
        Mockito.`when`(storiesRepository.getStories()).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(storiesRepository)
        val actualStories: PagingData<Stories> = mainViewModel.allStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}