package com.example.projectfoodmanager.util

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified

class PreloadCalenderEntriesModelProvider(
    private var items: MutableList<CalenderEntry>,
    private val context: Context
) : ListPreloader.PreloadModelProvider<String> {

    override fun getPreloadItems(position: Int): MutableList<String> {
        val item = items[position]
        val preloadUrls = mutableListOf<String>()

        item.recipe.imgSource.let { preloadUrls.add(it) }

        return preloadUrls
    }

    override fun getPreloadRequestBuilder(url: String): RequestBuilder<*> {
        return Glide.with(context)
            .load(url)
            .override(100, 100)
    }

    fun getItems(): MutableList<CalenderEntry> = this.items.toMutableList()

    fun addItems(newItems: MutableList<CalenderEntry>) {
        println()
        this.items.addAll(newItems)
        println()
    }

    fun setItems(newItems: MutableList<CalenderEntry>) {
        this.items = newItems
        println()
    }
}