package com.example.projectfoodmanager.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified

abstract class BaseAdapter<T, VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<BaseAdapter<T, VB>.BaseViewHolder>() {

    protected var itemList: MutableList<T> = mutableListOf()

    open var imagesToLoad: Int = DEFAULT_NR_OF_IMAGES_BY_RECIPE_CARD
    var imagesLoaded: Int = 0

    abstract fun bind(binding: VB, item: T, position: Int)

    inner class BaseViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(holder.binding, this.itemList[position], position)
    }


    override fun getItemCount(): Int = this.itemList.size

    fun getItems(): MutableList<T> = this.itemList.toMutableList()

    fun setItems(items: MutableList<T>) {
        removeItems()
        this.itemList = items
        notifyItemRangeChanged(0,this.itemList.size)
    }

    fun addItem(item: T) {
        this.itemList.add(item)
        notifyItemInserted(this.itemList.size)
    }

    fun addItem(item: T,position: Int) {
        this.itemList.add(position,item)
        notifyItemInserted(position)
    }

    fun addItems(_list: MutableList<T>) {
        val oldSize = this.itemList.size
        this.itemList.addAll(_list)
        notifyItemRangeChanged(oldSize-1, _list.size)
    }

    fun updateItem(position: Int,item: T){
        this.itemList.removeAt(position)
        this.itemList.add(position,item)
        notifyItemChanged(position)
    }

    fun updateItems(position: Int,_list: MutableList<T>){
        this.itemList.subList(position,position+_list.size).clear()
        this.itemList.addAll(position,_list)
        notifyItemRangeChanged(position,_list.size)
    }



    fun removeItem(position: Int) {
        if (position in itemList.indices) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun removeItems(){
        val listSize = itemList.size
        itemList = mutableListOf()
        imagesLoaded = 0
        notifyItemRangeRemoved(0,listSize)
    }



}
