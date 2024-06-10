package com.example.storyapp.ui.detailStory

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.Stories
import com.example.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityDetailStoryBinding.inflate(layoutInflater).apply {
            setContentView(root)
            binding = this
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getDetailItem()
    }

    private fun getDetailItem() {
        intent.getParcelableExtra<Stories>(EXTRA_NAME)?.let { detail ->
            supportActionBar?.title = detail.name
            binding.run {
                ivDetailName.text = detail.name
                Glide.with(this@DetailStoryActivity)
                    .load(detail.photoUrl)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivDetailPhoto)
                ivDetailDescription.text = detail.description
                tvLatitude.text = detail.lat.toString()
                tvLongitude.text = detail.lon.toString()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
    }
}