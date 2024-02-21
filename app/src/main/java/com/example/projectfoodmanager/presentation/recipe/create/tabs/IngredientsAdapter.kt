package com.example.projectfoodmanager.presentation.recipe.create.tabs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.IngredientQuantity
import com.example.projectfoodmanager.databinding.ItemRecipeIngredientsLayoutNewBinding


class IngredientsAdapter(
    items: List<IngredientQuantity>,
    val onUpdateClicked: (Int, IngredientQuantity) -> Unit,
) : RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>() {

    private val TAG: String? = "IngredientsAdapter"
    private var list: MutableList<IngredientQuantity> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemRecipeIngredientsLayoutNewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<IngredientQuantity>){
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int,item: IngredientQuantity){
        list.removeAt(position)
        //var ingrid = list.get(position)

        list.add(position,item)
        notifyItemChanged(position)
    }


    fun addItem(item: IngredientQuantity){
        // Add the new item to the data set
        list.add(item)

        // Notify the adapter that the data set has changed
        // Notify the adapter that an item was inserted

        notifyDataSetChanged()

    }


    fun getItems(): List<IngredientQuantity> {
        return list
    }

    fun cleanList(){
        this.list= arrayListOf()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(private val binding: ItemRecipeIngredientsLayoutNewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IngredientQuantity) {

            binding.updateIB.visibility= View.VISIBLE
            binding.removeIB.visibility= View.VISIBLE

            binding.updateIB.setOnClickListener {
                //updateItem(bindingAdapterPosition,item)
                onUpdateClicked.invoke(bindingAdapterPosition,item)
            }


            binding.removeIB.setOnClickListener {
                removeItem(bindingAdapterPosition)
            }

            // todo
            //binding.nameIngridTV.text= item.ingredient.name
        }
    }




}
