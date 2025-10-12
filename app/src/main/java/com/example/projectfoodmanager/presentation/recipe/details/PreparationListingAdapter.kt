package com.example.projectfoodmanager.presentation.recipe.details;

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Preparation
import com.example.projectfoodmanager.databinding.ItemRecipePreparationLayoutBinding



class PreparationListingAdapter(
    val context: Context?,
) : RecyclerView.Adapter<PreparationListingAdapter.MyViewHolder>() {

    private val TAG: String = "CalenderEntryAdapter"
    var list: MutableList<Preparation> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemRecipePreparationLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<Preparation>) {
        this.list = list
        notifyItemRangeChanged(0, list.size)
    }

    fun updateItem(position: Int, item: Preparation) {
        list.removeAt(position)
        list.add(position, item)
        notifyItemChanged(position)
    }


    fun cleanList() {
        val oldSize = list.size
        list.clear()
        if (oldSize > 0) {
            notifyItemRangeRemoved(0, oldSize)
        }
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(private val binding: ItemRecipePreparationLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Preparation) {


            binding.stepTV.text = item.step.toString()
            binding.numberStepTv.text = context?.getString(R.string.ITEM_PREPARATION, item.step)
            binding.descriptionTV.text = item.description

            if (list.last() == item)
                binding.lineV.isVisible=false
        }
    }
}
