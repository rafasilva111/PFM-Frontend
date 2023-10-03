package com.example.projectfoodmanager.presentation.recipe.details;

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.IngredientQuantity
import com.example.projectfoodmanager.databinding.ItemRecipeIngredientsLayoutBinding


class IngredientListingAdapter(
    val context: Context?,
) : RecyclerView.Adapter<IngredientListingAdapter.MyViewHolder>() {

    private val TAG: String = "CalenderEntryAdapter"
    var list: MutableList<IngredientQuantity> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemRecipeIngredientsLayoutBinding.inflate(
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

    fun updateList(list: MutableList<IngredientQuantity>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, item: IngredientQuantity) {
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


    inner class MyViewHolder(private val binding: ItemRecipeIngredientsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IngredientQuantity) {

            binding.nameIngridTV.text = item.ingredient.name.capitalize()
            // ignore warning
            if (item.quantity_normalized  != null && item.units_normalized!=null)
                binding.quantIngridTV.text = item.quantity_normalized.toString() + " "+item.units_normalized



        }
    }
}