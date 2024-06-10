package com.example.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.Stories
import com.example.storyapp.databinding.ItemStoriesRowBinding
import com.example.storyapp.ui.detailStory.DetailStoryActivity
import com.example.storyapp.utils.DateFormatter
import java.util.TimeZone

class StoriesAdapter(private val diffCallback: DiffUtil.ItemCallback<Stories> = DIFF_CALLBACK) :
    PagingDataAdapter<Stories, StoriesAdapter.StoryViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoriesRowBinding.inflate(inflater, parent, false)
        return StoryViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.run { holder.bind(this) }
    }

    inner class StoryViewHolder(private val binding: ItemStoriesRowBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(story: Stories) {
            binding.run {
                tvItemName.text = story.name
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.baseline_image_24)
                    .error(R.drawable.baseline_error_24)
                    .into(ivItemPhoto)
                tvItemCreatedAt.text = DateFormatter.formatDate(story.createdAt, TimeZone.getDefault().id)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java).apply {
                        putExtra(DetailStoryActivity.EXTRA_NAME, story)
                    }
                    val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(ivItemPhoto, "image"),
                        Pair(tvItemName, "name")
                    )
                    Log.d("StoryAdapter", "Clicked on: $story")
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stories>() {
            override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem == newItem
            }
        }
    }
}