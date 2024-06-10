package com.example.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.Api.SignupResponse
import com.example.storyapp.data.StoriesRepository
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.MainDispatcherRule
import com.example.storyapp.utils.MyResult
import com.example.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TestRegisterViewModel {

    private lateinit var viewModelRegister: RegisterViewModel

    @get:Rule
    val ruleInstantExecutor = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val ruleMainDispatcher = MainDispatcherRule()

    @Mock
    private lateinit var repositoryStories: StoriesRepository

    @Before
    fun initialize() {
        viewModelRegister = RegisterViewModel(repositoryStories)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test Get All User Register is successful`() = runBlockingTest {
        val registerDummy = DataDummy.generateDummySignUpResponseSuccess()
        val registerExpected = MutableLiveData<MyResult<SignupResponse>>()
        registerExpected.value = MyResult.Success(registerDummy)

        Mockito.`when`(repositoryStories.register("MyName", "my@gmail.com","mypassword")).thenReturn(registerExpected)

        val responseActual = viewModelRegister.getAllUserRegister("MyName", "my@gmail.com","mypassword").getOrAwaitValue()

        Mockito.verify(repositoryStories).register("MyName", "my@gmail.com","mypassword")
        assertNotNull(responseActual)
        assertTrue(responseActual is MyResult.Success)
        assertSame(registerDummy, (responseActual as MyResult.Success).list)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test Get All User Register is failed`() = runBlockingTest {
        val registerExpected = MutableLiveData<MyResult<SignupResponse>>()
        registerExpected.value = MyResult.Error("Error")

        Mockito.`when`(repositoryStories.register("MyName", "my@gmail.com","mypassword")).thenReturn(registerExpected)

        val responseActual = viewModelRegister.getAllUserRegister("MyName", "my@gmail.com","mypassword").getOrAwaitValue()

        Mockito.verify(repositoryStories).register("MyName", "my@gmail.com","mypassword")
        assertNotNull(responseActual)
        assertTrue(responseActual is MyResult.Error)
    }
}