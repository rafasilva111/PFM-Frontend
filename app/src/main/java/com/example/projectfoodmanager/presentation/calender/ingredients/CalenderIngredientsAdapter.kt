package com.example.projectfoodmanager.presentation.calender.ingredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderIngredient
import com.example.projectfoodmanager.databinding.ItemCalenderIngredientEntryBinding


class CalenderIngredientsAdapter(
    val onItemClicked: (Int, CalenderIngredient) -> Unit,
) : RecyclerView.Adapter<CalenderIngredientsAdapter.MyViewHolder>() {

    private val TAG: String? = "CalenderEntryAdapter"
    private var list: MutableList<CalenderIngredient> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemCalenderIngredientEntryBinding.inflate(
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

    fun updateList(list: MutableList<CalenderIngredient>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, item: CalenderIngredient) {
        list.removeAt(position)
        list.add(position, item)
        notifyItemChanged(position)
    }


    fun cleanList() {
        this.list = arrayListOf()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(private val binding: ItemCalenderIngredientEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalenderIngredient) {


            binding.nameTV.text = item.name
            binding.quantityTV.text = item.quantity.toString() + " " + item.units


        }
    }
}