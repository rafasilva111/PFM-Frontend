package com.example.projectfoodmanager.presentation.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.util.RecipeDifficultyConstants
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.example.projectfoodmanager.util.SharedPreference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class RecipeListingAdapter(
    val onItemClicked: (Int, Recipe) -> Unit,
    val onLikeClicked: (Recipe, Boolean) -> Unit,
    val onSaveClicked: (Recipe, Boolean) -> Unit,
    private val sharedPreference: SharedPreference
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

    fun updateItem(position: Int,item: Recipe){
        list.removeAt(position)
        list.add(position,item)
        notifyItemChanged(position)
    }


    fun cleanList(){
        this.list= arrayListOf()
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
            if (!item.img_source.isNullOrEmpty()){
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
                //Firebase.storage.reference.putFile()

            }

            binding.authorTV.text = item.company
            val imgRef = Firebase.storage.reference.child(item.img_source)
            imgRef.downloadUrl.addOnSuccessListener { Uri ->
                val imageURL = Uri.toString()
                Glide.with(binding.authorIV.context).load(imageURL).into(binding.authorIV)
            }

            //TODO: Ver com o Rafa -> author verificado ou não

            //TODO: Ver com o Rafa -> receita é verificada ou não

            //TODO: Ver com o Rafa -> Recolher apenas a data e não a data e o horas
            binding.dateTV.text = item.created_date
            binding.recipeTitleTV.text = item.title
            binding.recipeDescriptionTV.text = item.description.toString()
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }
            binding.nLikeTV.text = item.likes.toString()

            // get user from shared prefrences
            val user = sharedPreference.getUserSession()

            binding.ratingRecipeRB.rating = item.source_rating.toFloat()
            binding.ratingMedTV.text = item.source_rating.toString()


            binding.timeTV.text = item.time
            binding.difficultyTV.text = item.difficulty

            when(item.difficulty){
                RecipeDifficultyConstants.LOW->{
                    binding.difficultyIV.setImageResource(R.drawable.low_difficulty)
                }
                RecipeDifficultyConstants.MEDIUM->{
                    binding.difficultyIV.setImageResource(R.drawable.medium_difficulty)
                }
                RecipeDifficultyConstants.HIGH->{
                    binding.difficultyIV.setImageResource(R.drawable.high_difficulty)
                }
            }

            binding.portionTV.text = item.portion


            //--------- LIKES ---------
            //TODO: Ver com o Rafa -> LIKES
            // check for user likes
            if (user!=null){
                if(user!!.checkIfLiked(item) != -1){
                    binding.likeIB.setImageResource(R.drawable.ic_like_active)
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
            //TODO: Ver com o Rafa -> DETAIL VIEW ESTA ASSIM

            /*  binding.likeIB.setOnClickListener {
             if (sharedPreference.getUserSession()!!.checkIfLiked(item) == -1) {
                 recipeViewModel.addLikeOnRecipe(recipe.id)
             } else {
                 recipeViewModel.removeLikeOnRecipe(recipe.id)
             }
         }*/

            // favorite function
//            binding.favoritesIB.setImageResource(R.drawable.ic_favorite)

            // check for user likes

            //--------- FAVORITES ---------
            //TODO: Ver com o Rafa -> FAVORITES
            if (user!=null){
                if(user!!.checkIfSaved(item) != -1){
                    binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
                }
                else
                    binding.favoritesIB.setImageResource(R.drawable.ic_favorito_black)
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

 /*           binding.favoritesIB.setOnClickListener {
                if (sharedPreference.getUserSession()!!.checkIfSaved(recipe) == -1) {
                    recipeViewModel.addSaveOnRecipe(recipe.id)
                } else {
                    recipeViewModel.removeSaveOnRecipe(recipe.id)
                }
            }*/

        }
    }
}
