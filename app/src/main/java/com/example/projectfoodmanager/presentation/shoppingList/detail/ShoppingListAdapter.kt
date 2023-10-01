package com.example.projectfoodmanager.presentation.shoppingList.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingIngredient
import com.example.projectfoodmanager.databinding.ItemShoppingIngredientBinding


class ShoppingListAdapter(
    val context: Context,
    val onItemClicked: (Int, ShoppingIngredient) -> Unit,
) : RecyclerView.Adapter<ShoppingListAdapter.MyViewHolder>() {

    private val TAG: String = "ShoppingListListingAdapter"
    var list: MutableList<ShoppingIngredient> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemShoppingIngredientBinding.inflate(
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

    fun updateList(list: MutableList<ShoppingIngredient>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, item: ShoppingIngredient) {
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


    inner class MyViewHolder(private val binding: ItemShoppingIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingIngredient) {
            binding.nameTV.text = item.ingredient.name
            binding.quantityTV.text = context.getString(R.string.quantity_placeholder,item.quantity,item.units)
        }
    }
}