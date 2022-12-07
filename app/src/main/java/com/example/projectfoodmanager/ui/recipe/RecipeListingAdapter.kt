package com.example.projectfoodmanager.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.Recipe_info
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding


class RecipeListingAdapter(
    val onItemClicked: (Int, Recipe) -> Unit,
    val onEditClicked: (Int, Recipe) -> Unit,
) : RecyclerView.Adapter<RecipeListingAdapter.MyViewHolder>() {



    private var list: MutableList<Recipe> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemRecipeLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<Recipe>){
        this.list = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(val binding: ItemRecipeLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe){

            binding.dateLabel.text = item.info.date.toString()
            binding.recipeTitle.text = item.info.title.toString()
            binding.like.setOnClickListener { onEditClicked.invoke(adapterPosition,item) }
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition,item) }

            Glide.with(binding.imageView.context).load(item.storageReference).into(binding.imageView)

        }
    }
}