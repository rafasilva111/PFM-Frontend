package com.example.projectfoodmanager.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<BaseAdapter<T, VB>.BaseViewHolder>() {

    private var itemList: MutableList<T> = mutableListOf()

    open var imagesToLoad: Int = 4
    var imagesLoaded: Int = 0

    abstract fun bind(binding: VB, item: T, position: Int)

    inner class BaseViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(holder.binding, itemList[position], position)
    }

    override fun getItemCount(): Int = itemList.size

    fun getItems(): MutableList<T> = itemList.toMutableList()

    fun setItems(items: MutableList<T>) {
        removeItems()
        itemList = items
        notifyItemRangeChanged(0,itemList.size)
    }

    fun addItem(item: T) {
        itemList.add(item)
        notifyItemInserted(itemList.size - 1)
    }

    fun addItems(_list: MutableList<T>){
        val listSize = itemList.size
        itemList = (itemList + _list).toMutableList()
        notifyItemRangeChanged(listSize,itemList.size)
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
