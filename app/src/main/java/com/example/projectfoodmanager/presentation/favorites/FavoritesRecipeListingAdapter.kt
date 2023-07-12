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
            binding.authorTV.text = item.company

            //TODO: Ver com o Rafa -> inconcistencia comparado com o recipe listing adptar
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



            //TODO: Ver com o Rafa -> author verificado ou não

            //TODO: Ver com o Rafa -> receita é verificada ou não

            binding.dateTV.text = item.created_date
            binding.recipeTitleTV.text = item.title
            binding.recipeDescriptionTV.text = item.description.toString()
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }


            //TODO: Ver com o Rafa -> [get user from shared prefrences ]
            // inconcistencia comparado com o recipe listing adptar


            binding.ratingRecipeRB.rating = item.source_rating.toFloat()
            binding.ratingMedTV.text = item.source_rating.toString()

            binding.timeTV.text = item.time
            binding.difficultyTV.text = item.difficulty
            binding.portionTV.text = item.portion

            //--------- LIKES ---------
            //TODO: Ver com o Rafa -> LIKES
            // check for user likes

            if (user!=null){
                if(user!!.checkIfLiked(item) != -1){
                    binding.favoritesIB.setImageResource(R.drawable.ic_like_active)
                }
                else
                    binding.likeIB.setImageResource(R.drawable.ic_like_black)
            }

            binding.likeIB.setOnClickListener {
                if(user!!.checkIfLiked(item) == -1) {
                    onLikeClicked.invoke(item, true)
                }
                else
                {
                    onLikeClicked.invoke(item, false)
                }
            }

            //TODO: Ver com o Rafa ->
/*
            // favorite function
            binding.favoritesIB.setImageResource(R.drawable.ic_favorite)
*/
            //--------- FAVORITES ---------
            //TODO: Ver com o Rafa -> FAVORITES
            // check for user FAVORITES

            if (user!=null){
                if(user!!.checkIfSaved(item) != -1){
                    binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
                }
                else
                    binding.favoritesIB.setImageResource(R.drawable.ic_favorite)
            }

            binding.favoritesIB.setOnClickListener {
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
