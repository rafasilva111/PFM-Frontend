package com.example.projectfoodmanager.util

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified

class RecipePreloadModelProvider(
    private var items: MutableList<RecipeSimplified>,
    private val context: Context
) : ListPreloader.PreloadModelProvider<String> {

    override fun getPreloadItems(position: Int): MutableList<String> {
        val item = items[position]
        val preloadUrls = mutableListOf<String>()

        item.imgSource.let { preloadUrls.add(it) }
        item.createdBy.imgSource.let { preloadUrls.add(it) }

        return preloadUrls
    }

    override fun getPreloadRequestBuilder(url: String): RequestBuilder<*> {
        return Glide.with(context)
            .load(url)
            .override(100, 100)
    }

    fun getItems(): MutableList<RecipeSimplified> = this.items.toMutableList()

    fun addItems(newItems: MutableList<RecipeSimplified>) {
        println()
        this.items.addAll(newItems)
        println()
    }

    fun setItems(newItems: MutableList<RecipeSimplified>) {
        this.items = newItems
        println()
    }
}