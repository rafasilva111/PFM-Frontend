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
    private val authModel: AuthViewModel
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
            authModel.getSession { user ->
                if (user != null) {
                    if ( user.favorite_recipes.indexOf(item.id)!=-1){
                        binding.like.setImageResource(R.drawable.ic_like_red)
                    }
                }
            }





            binding.like.setOnClickListener {
                authModel.getSession { user ->
                    if (user != null){
                        if ( user.favorite_recipes.indexOf(item.id)!=-1){
                            authModel.removeFavoriteRecipe(item)
                            binding.like.setImageResource(R.drawable.ic_favorito)
                            Toast.makeText(it.context,"Receita removida dos favoritos.", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            authModel.addFavoriteRecipe(item)
                            binding.like.setImageResource(R.drawable.ic_like_red)
                            Toast.makeText(it.context,"Receita adicionada aos favoritos.", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }


        }
    }
}