package com.example.pedulipasal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R
import com.example.pedulipasal.databinding.ItemCategoryBinding

class NewsCategoryAdapter(
    private val categories: List<String>,
    private val onCategorySelected: (String) -> Unit,
    defaultCategory: String? = null
) : RecyclerView.Adapter<NewsCategoryAdapter.ViewHolder>() {

    private var selectedCategory: String? = defaultCategory

    inner class ViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            binding.tvCategory.text = category

            if (category == selectedCategory) {
                binding.categoryItemContainer.setBackgroundResource(R.drawable.bg_rounded_news_category_selected)
                binding.tvCategory.setTextColor(binding.root.context.getColor(R.color.md_theme_onTertiary_highContrast))
            } else {
                binding.categoryItemContainer.setBackgroundResource(R.drawable.bg_rounded_news_category_default)
                binding.tvCategory.setTextColor(binding.root.context.getColor(R.color.md_theme_onSurface_highContrast))
            }

            binding.categoryItemContainer.setOnClickListener {
                selectedCategory = category
                notifyDataSetChanged() // Refresh all items to update their background
                onCategorySelected(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}
