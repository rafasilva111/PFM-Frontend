package com.example.projectfoodmanager.presentation.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.presentation.viewmodels.RecipeViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class FavoritesRecipeListingAdapter(
    val onItemClicked: (Int, Recipe) -> Unit,
    val authViewModel: AuthViewModel,
    val recipeViewModel: RecipeViewModel
) : RecyclerView.Adapter<FavoritesRecipeListingAdapter.MyViewHolder>() {


    private var user: User? = null
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

    fun updateList(list: MutableList<Recipe>, user: User){
        this.list = list
        this.user = user
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
            val imgRef = Firebase.storage.reference.child(item.img_source)
            imgRef.downloadUrl.addOnSuccessListener { Uri ->
                val imageURL = Uri.toString()
                Glide.with(binding.imageView.context).load(imageURL).into(binding.imageView)
            }
                .addOnFailureListener {
                    Glide.with(binding.imageView.context)
                        .load(R.drawable.good_food_display___nci_visuals_online)
                        .into(binding.imageView)
                }
            binding.dateLabel.text = item.created_date
            binding.recipeTitle.text = item.title
            binding.TVAutor.text = item.company
            binding.TVDescription.text = item.description.toString()
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }


            if (item.likes == 1) {
                binding.TVRate.text = "1 Gosto"
            } else {
                binding.TVRate.text = "${item.likes} Gostos"
            }

            // like function


            if (user!=null){
                if(user!!.checkIfLiked(item) != -1){
                    binding.like.setImageResource(R.drawable.ic_like_active)
                }
                else
                    binding.like.setImageResource(R.drawable.ic_like)
            }

            /*binding.like.setOnClickListener {
                if(user!!.checkIfLiked(item) == -1) {
                    onLikeClicked.invoke(item, true)
                }
                else
                {
                    onLikeClicked.invoke(item, false)
                }
            }*/

            // favorite function
            binding.saved.setImageResource(R.drawable.ic_favorite)

            // check for user likes

            if (user!=null){
                if(user!!.checkIfSaved(item) != -1){
                    binding.saved.setImageResource(R.drawable.ic_favorito_active)
                }
                else
                    binding.saved.setImageResource(R.drawable.ic_favorite)
            }

            /*binding.saved.setOnClickListener {
                if(user!!.checkIfSaved(item) == -1) {
                    onSaveClicked.invoke(item, true)
                }
                else
                {
                    onSaveClicked.invoke(item, false)
                }
            }*/


            //set initial sates
            /*authModel.getUserSession_old { user ->
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
                authModel.getUserSession_old { user ->
                    if (user != null) {
                        if (user.getFavoriteRecipe(item.id) != null) {
                            authModel.removeFavoriteRecipe(item)
                            binding.favorites.setImageResource(R.drawable.ic_favorite)
                            Toast.makeText(
                                it.context,
                                "Receita removida dos guardados.",
                                Toast.LENGTH_SHORT
                            ).show()
                            list.remove(item)

                        } else {
                            authModel.addFavoriteRecipe(item)
                            binding.favorites.setImageResource(R.drawable.ic_favorito_white)
                        }
                    }
                }
            }
            binding.like.setOnClickListener {
                authModel.getUserSession_old { user ->
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
                            Toast.makeText(
                                it.context,
                                "Receita removida dos favoritos.",
                                Toast.LENGTH_SHORT
                            ).show()
                            list.remove(item)


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

            }*/
        }
    }
}
