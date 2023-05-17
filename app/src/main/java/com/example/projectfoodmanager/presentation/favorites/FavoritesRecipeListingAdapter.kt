package com.example.projectfoodmanager.presentation.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class FavoritesRecipeListingAdapter(
    val onItemClicked: (Int, Recipe) -> Unit,
    val onLikeClicked: (Recipe, Boolean) -> Unit,
    val onSaveClicked: (Recipe, Boolean) -> Unit,
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

    fun getAdapterList():MutableList<Recipe>{
        return this.list
    }

    fun updateList(list: MutableList<Recipe>, user: User){
        this.list = list
        this.user = user
        notifyDataSetChanged()
    }

    fun updateItem(position: Int,item: Recipe,user: User){
        list.removeAt(position)
        list.add(position,item)
        this.user = user
        notifyItemChanged(position)
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

            binding.like.setOnClickListener {
                if(user!!.checkIfLiked(item) == -1) {
                    onLikeClicked.invoke(item, true)
                }
                else
                {
                    onLikeClicked.invoke(item, false)
                }
            }

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

            binding.saved.setOnClickListener {
                if(user!!.checkIfSaved(item) == -1) {
                    onSaveClicked.invoke(item, true)
                }
                else
                {
                    onSaveClicked.invoke(item, false)
                }
            }
        }
    }
}
