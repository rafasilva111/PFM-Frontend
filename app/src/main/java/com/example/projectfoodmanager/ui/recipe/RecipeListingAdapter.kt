package com.example.projectfoodmanager.ui.recipe

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.ui.auth.AuthViewModel
import com.example.projectfoodmanager.util.UiState
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class RecipeListingAdapter(
    val onItemClicked: (Int, Recipe) -> Unit,
    private val authModel: AuthViewModel,
    private val viewModel: RecipeViewModel
) : RecyclerView.Adapter<RecipeListingAdapter.MyViewHolder>() {


    private val TAG: String? = "RecipeListingAdapter"
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


    inner class MyViewHolder(private val binding: ItemRecipeLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Recipe) {
            val imgRef = Firebase.storage.reference.child(item.img)
            imgRef.downloadUrl.addOnSuccessListener { Uri ->
                val imageURL = Uri.toString()
                Glide.with(binding.imageView.context).load(imageURL).into(binding.imageView)
            }
                .addOnFailureListener {
                    Glide.with(binding.imageView.context)
                        .load(R.drawable.good_food_display___nci_visuals_online)
                        .into(binding.imageView)
                }
            binding.dateLabel.text = item.date
            binding.recipeTitle.text = item.title
            binding.TVDescription.text = item.desc.toString()
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }

            // like function

            if (item.likes.size == 1) {
                binding.TVRate.text = "1 Gosto"
            } else {
                binding.TVRate.text = item.likes.size.toString() + " Gosto"
            }
            binding.dateLabel.text = item.date


            // favorite function
            binding.favorites.setImageResource(R.drawable.ic_favorite)
            binding.like.setImageResource(R.drawable.ic_like)
            authModel.getUserSession { user ->
                if (user != null) {
                    val recipe_fav = user.getFavoriteRecipe(item.id)
                    if (recipe_fav != null){
                        binding.favorites.setImageResource(R.drawable.ic_favorito_white)
                        if (recipe_fav != item){
                            user.removeFavoriteRecipe(recipe_fav.id)
                            user.addFavoriteRecipe(item)
                            authModel.updateUserSession(user) { state ->
                                when (state) {
                                    is UiState.Success -> {
                                        Log.d(TAG, "bind: Updated recipe " + state.data.toString())
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }

                    val recipe_liked = user.getLikedRecipe(item.id)
                    Log.d(TAG, "set like icon: "+recipe_liked +"  "+ item.title)
                    if (recipe_liked != null){
                        binding.like.setImageResource(R.drawable.ic_like_red)
                        if (recipe_liked != item){
                            user.removeLikeRecipe(recipe_liked.id)
                            user.addLikeRecipe(item)
                            authModel.updateUserSession(user) { state ->
                                when (state) {
                                    is UiState.Success -> {
                                        Log.d(TAG, "bind: Updated recipe " + state.data.toString())
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            binding.favorites.setOnClickListener {
                authModel.getUserSession { user ->
                    if (user != null) {
                        if (user.getFavoriteRecipe(item.id) != null) {
                            authModel.removeFavoriteRecipe(item)
                            binding.favorites.setImageResource(R.drawable.ic_favorite)
                            Toast.makeText(
                                it.context,
                                "Receita removida dos favoritos.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            authModel.addFavoriteRecipe(item)
                            binding.favorites.setImageResource(R.drawable.ic_favorito_white)
                        }
                    }
                }
            }
            binding.like.setOnClickListener {
                authModel.getUserSession { user ->
                    if (user != null) {
                        if (user.getLikedRecipe(item.id) != null) {
                            authModel.removeLikeOnRecipe(item)
                            viewModel.removeLikeOnRecipe(user.id,item)
                            binding.like.setImageResource(R.drawable.ic_like)
                            if (item.likes.size == 1) {
                                binding.TVRate.text = "1 Gosto"
                            } else {
                                binding.TVRate.text = item.likes.size.toString() + " Gosto"
                            }


                        } else {
                            authModel.addLikeOnRecipe(item)
                            viewModel.addLikeOnRecipe(user.id,item)
                            binding.like.setImageResource(R.drawable.ic_like_red)
                            if (item.likes.size == 1) {
                                binding.TVRate.text = "1 Gosto"
                            } else {
                                binding.TVRate.text = item.likes.size.toString() + " Gosto"
                            }
                        }

                    }
                }

            }
        }
    }
}
