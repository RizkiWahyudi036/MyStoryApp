package com.ari.submission.storyapp.ui.login

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.Api.LoginResponse
import com.example.storyapp.data.StoriesRepository
import com.example.storyapp.ui.Login.LoginViewModel
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.MainDispatcherRule
import com.example.storyapp.utils.MyResult
import com.example.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)

class LoginViewModelTest{
    private lateinit var loginViewModel: LoginViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository

    @Mock
    private lateinit var context: Context

    @Before
    fun setUp(){
        loginViewModel = LoginViewModel(storiesRepository, context)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get All User Login is called and return success`() = runTest {
        val loginDummy = DataDummy.generateDummyLoginResponseSuccess()
        val expectedLogin = MutableLiveData<MyResult<LoginResponse>>()
        expectedLogin.value = MyResult.Success(loginDummy)

        Mockito.`when`(storiesRepository.login("email123@gmail.com", "password123")).thenReturn(expectedLogin)

        val actualResponse = loginViewModel.getAllUserLogin("email123@gmail.com","password123").getOrAwaitValue()

        Mockito.verify(storiesRepository).login("email123@gmail.com","password123")
        assertNotNull(actualResponse)
        assertTrue(actualResponse is MyResult.Success)
        assertSame(loginDummy, (actualResponse as MyResult.Success).list)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get All User Login is failed and return error`() = runTest {
        val expectedLogin = MutableLiveData<MyResult<LoginResponse>>()
        expectedLogin.value = MyResult.Error("Error")

        Mockito.`when`(storiesRepository.login("email123@gmail.com", "password123")).thenReturn(expectedLogin)

        val actualResponse = loginViewModel.getAllUserLogin("email123@gmail.com","password123").getOrAwaitValue()

        Mockito.verify(storiesRepository).login("email123@gmail.com","password123")
        assertNotNull(actualResponse)
        assertTrue(actualResponse is MyResult.Error)

    }
}