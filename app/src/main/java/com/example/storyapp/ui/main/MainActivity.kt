package com.example.storyapp.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoriesAdapter
import com.example.storyapp.data.Stories
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.preferences.SharedPreferences
import com.example.storyapp.ui.Login.LoginActivity
import com.example.storyapp.ui.Maps.MapsActivity
import com.example.storyapp.ui.addStory.AddStoryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoriesAdapter
    private lateinit var sph: SharedPreferences

    private var storyName: ArrayList<String>? = null
    private var storyDesc: ArrayList<String>? = null
    private var storyLat: ArrayList<Float>? = null
    private var storyLong: ArrayList<Float>? = null

    private val viewModel by viewModels<MainViewModel> {
        MainViewModel.Factory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sph = SharedPreferences(this)

        initializeStories()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeStories() {
    val token = sph.getUserToken()
    viewModel.allStory.observe(this) {
        adapter.submitData(lifecycle, it)
    }

        storyName = ArrayList()
        storyDesc = ArrayList()
        storyLat = ArrayList()
        storyLong = ArrayList()
        viewModel.getAllStories("Bearer $token")

        viewModel.getStoriesPaging().observe(this) { stories ->
            binding.progressBar.visibility = View.VISIBLE

            clearStoryData()

            for (story in stories) {
                addStoryData(story)
            }

            adapter.notifyDataSetChanged()

            binding.progressBar.visibility = View.GONE
        }

        setupRecyclerView()
    }

    private fun clearStoryData() {
        storyName?.clear()
        storyDesc?.clear()
        storyLat?.clear()
        storyLong?.clear()
    }

    private fun addStoryData(story: Stories) {
        storyName?.add(story.name.toString())
        storyDesc?.add(story.description.toString())
        storyLat?.add(story.lat.toString().toFloat())
        storyLong?.add(story.lon.toString().toFloat())
    }

    private fun setupRecyclerView() {
        adapter = StoriesAdapter()

        binding.apply {
            rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStories.setHasFixedSize(true)
            rvStories.adapter = adapter.withLoadStateHeaderAndFooter(
                header = LoadingStateAdapter {
                    adapter.retry()
                },
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
        }
    }

    private fun getNewStories() {
        binding.apply {
            if (intent != null) {
                val isNewStory = intent.extras?.getBoolean(SUCCESS_UPLOAD_STORY)
                if (isNewStory != null && isNewStory) {
                    initializeStories()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_logout -> logoutUser()
            R.id.btn_add_photo -> navigateToAddStoryActivity()
            R.id.btn_maps -> navigateToMapsActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() {
        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("Log Out")
            setMessage("Are you sure to log out?")
            setPositiveButton("Yes") { dialogInterface, which ->
                sph.clearUserLogin()
                sph.clearUserToken()
                sph.setStatusLogin(false)
                navigateToLoginActivity()
            }
            setNegativeButton("Cancel") { dialogInterface, which ->
                Toast.makeText(this@MainActivity, "Okay! Have a nice use StoryApp", Toast.LENGTH_SHORT).show()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(true)
            alertDialog.show()
        }
    }

    private fun navigateToLoginActivity() {
        Intent(this@MainActivity, LoginActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun navigateToAddStoryActivity() {
        Intent(this, AddStoryActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun navigateToMapsActivity() {
        Intent(this, MapsActivity::class.java).apply {
            startActivity(this)
        }
    }

    companion object {
        const val SUCCESS_UPLOAD_STORY = "success upload story"
    }
}