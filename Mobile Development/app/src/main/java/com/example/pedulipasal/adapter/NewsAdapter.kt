package com.example.pedulipasal.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.response.NewsItem
import com.example.pedulipasal.databinding.ItemNewsBinding
import com.example.pedulipasal.page.detail.DetailNewsActivity

class NewsAdapter : ListAdapter<NewsItem, NewsAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val newsItem = getItem(position)
        holder.bind(newsItem)
    }

    class MyViewHolder(private val binding: ItemNewsBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newsItem: NewsItem) {
            binding.tvTitle.text = newsItem.title
            binding.tvDescription.text = newsItem.description

            Glide.with(binding.imgArticle.context)
                .load(newsItem.urlToImage)
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_broken_image_24)
                .into(binding.imgArticle)

            binding.root.setOnClickListener {
                val intent = Intent(context, DetailNewsActivity::class.java).apply {
                    putExtra("WEB_URL", newsItem.url)
                }
                context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsItem>() {
            override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
