package com.example.projectfoodmanager.presentation.profile.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.databinding.ItemProfileRecipeLayoutBinding
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import java.time.LocalDate


class ProfileRecipesAdapter(
    private val onItemClicked: (LocalDate) -> Unit,
) :
    RecyclerView.Adapter<ProfileRecipesAdapter.MyViewHolder>() {

    init {
        this.setHasStableIds(true)
    }

    var recipeListed: MutableList<RecipeSimplified> = mutableListOf()
    //private var currentSelected: TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemProfileRecipeLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ProfileRecipesAdapter.MyViewHolder, position: Int) {
        val item = recipeListed[position]
        holder.bind(item)
    }
    override fun getItemCount(): Int {
        return recipeListed.size
    }

    override fun getItemId(position: Int): Long {
        // Use a stable and unique identifier, for example, the recipe's ID
        return recipeListed[position].id.toLong()
    }



    fun updateList(daysInMonthArray: MutableList<RecipeSimplified>) {
        val firstPosition = recipeListed.size
        recipeListed += daysInMonthArray
        notifyItemRangeChanged(firstPosition,daysInMonthArray.size)
    }

    inner class MyViewHolder constructor(private val binding: ItemProfileRecipeLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: RecipeSimplified) {

            loadRecipeImage(binding.recipeIV, item.imgSource)

        }
    }



}