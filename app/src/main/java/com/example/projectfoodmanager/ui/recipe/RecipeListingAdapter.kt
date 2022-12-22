package com.example.projectfoodmanager.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.ui.auth.AuthViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class RecipeListingAdapter(
    val onItemClicked: (Int, Recipe) -> Unit,
    private val authModel: AuthViewModel,
    private val recipeModel: RecipeViewModel
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

    inner class MyViewHolder(private val binding: ItemRecipeLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Recipe){
            val imgRef = Firebase.storage.reference.child(item.img)
            imgRef.downloadUrl.addOnSuccessListener {Uri->
                val imageURL = Uri.toString()
                Glide.with(binding.imageView.context).load(imageURL).into(binding.imageView)
            }
                .addOnFailureListener {
                    Glide.with(binding.imageView.context).load(R.drawable.good_food_display___nci_visuals_online).into(binding.imageView)
                }
            binding.dateLabel.text = item.date
            binding.recipeTitle.text = item.title
            binding.TVDescription.text = item.desc.toString()
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition,item) }

            // favorite function

            authModel.getSession { user ->
                if (user != null) {
                    if ( user.favorite_recipes.indexOf(item.id)!=-1){
                        binding.favorites.setImageResource(R.drawable.ic_favorito_white)
                    }
                }
            }

            binding.like.setOnClickListener {
                authModel.getSession { user ->
                    if (user != null){
                        if ( user.favorite_recipes.indexOf(item.id)!=-1){
                            authModel.removeFavoriteRecipe(item)
                            binding.favorites.setImageResource(R.drawable.ic_favorite)
                            Toast.makeText(it.context,"Receita removida dos favoritos.", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            authModel.addFavoriteRecipe(item)
                            binding.favorites.setImageResource(R.drawable.ic_favorito_white)
                            Toast.makeText(it.context,"Receita adicionada aos favoritos.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // like function

            if (item.likes == 1){
                binding.TVRate.text = "1 Gosto"
            }
            else{
                binding.TVRate.text = item.likes.toString()+" Gosto"
            }
            binding.dateLabel.text = item.date

            authModel.getSession { user ->
                if (user != null) {
                    if ( user.liked_recipes.indexOf(item.id)!=-1){
                        binding.like.setImageResource(R.drawable.ic_like_red)
                    }
                }
            }

            binding.like.setOnClickListener {
                authModel.getSession { user ->
                    if (user != null){
                        if ( user.liked_recipes.indexOf(item.id)!=-1){
                            authModel.removeLikeOnRecipe(item)
                            recipeModel.removeLikeOnRecipe(item)
                            binding.like.setImageResource(R.drawable.ic_like)
                            Toast.makeText(it.context,"Removido gosto da receita.", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                        }
                        else{
                            authModel.addLikeOnRecipe(item)
                            recipeModel.addLikeOnRecipe(item)
                            binding.like.setImageResource(R.drawable.ic_like_red)
                            Toast.makeText(it.context,"Adicionado gosto á receita.", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}