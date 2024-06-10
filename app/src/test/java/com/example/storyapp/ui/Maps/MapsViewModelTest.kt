package com.example.storyapp.ui.Maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.Api.GetAllStoryResponse
import com.example.storyapp.data.StoriesRepository
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.MainDispatcherRule
import com.example.storyapp.utils.MyResult
import com.example.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest{
    private lateinit var mapsViewModel: MapsViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository

    @Before
    fun setUp(){
        mapsViewModel = MapsViewModel(storiesRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Stories With Location is called should not null and return success`() = runTest{
        val dummyMaps = DataDummy.generateDummyStoriesWithLoc()
        val expectedMaps = MutableLiveData<MyResult<GetAllStoryResponse>>()
        expectedMaps.value = MyResult.Success(dummyMaps)
        Mockito.`when`(storiesRepository.getStoriesWithLocation("AIzaSyB2uMeoJQiWDl8Illd-Co0nM74llsCN1Xk")).thenReturn(expectedMaps)

        val actualMaps = mapsViewModel.getStoriesWithLocation("AIzaSyB2uMeoJQiWDl8Illd-Co0nM74llsCN1Xk").getOrAwaitValue()
        assertNotNull(actualMaps)
        assertTrue(actualMaps is MyResult.Success)
        assertEquals(dummyMaps, (actualMaps as MyResult.Success).list)
    }
}