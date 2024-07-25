package com.example.projectfoodmanager.presentation.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.recipe.toRecipeSimplified
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class FavoritesRecipeListingAdapter(
    private val context: Context,
    val onItemClicked: (Int, RecipeSimplified) -> Unit,
    val onLikeClicked: (RecipeSimplified, Boolean) -> Unit,
    val onSaveClicked: (RecipeSimplified, Boolean) -> Unit,
) : RecyclerView.Adapter<FavoritesRecipeListingAdapter.MyViewHolder>() {


    private var user: User? = null
    private val TAG: String = "RecipeListingAdapter"
    var list: MutableList<RecipeSimplified> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemRecipeLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun getAdapterList():MutableList<RecipeSimplified>{
        return this.list
    }

    fun updateList(list: MutableList<RecipeSimplified>, user: User){
        this.list = list
        this.user = user

        notifyDataSetChanged()
    }

    fun updateList(list: MutableList<RecipeSimplified>){
        this.list = list

        notifyDataSetChanged()
    }


    fun concatList(list: MutableList<RecipeSimplified>){
        val initialSize = this.list.size
        this.list.addAll(list)

        notifyItemRangeInserted(initialSize, list.size)
    }

    fun updateItem(item: Recipe){

        for ((index, recipe) in list.withIndex()){
            if (recipe.id == item.id) {
                list[index] = item.toRecipeSimplified()
                notifyItemChanged(index)
                break
            }
        }

    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemChanged(position)
    }

    fun removeItem(item: Recipe){
        for ((index, recipe) in list.withIndex()){
            if (recipe.id == item.id) {
                list.removeAt(index)
                notifyItemRemoved(index)
                break
            }
        }

    }

    fun addItem(item: Recipe){
        list.add(item.toRecipeSimplified())
        notifyItemInserted( list.size)

    }

    override fun getItemCount(): Int {
        return list.size
    }



    inner class MyViewHolder(private val binding: ItemRecipeLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: RecipeSimplified) {

            /**
             * Loading Images
             */

            // Load Recipe img
            if (item.imgSource.isNotEmpty()) {
                val currentRecipeDrawable = binding.imageView.drawable
                val defaultRecipeDrawable = ContextCompat.getDrawable(context, R.drawable.default_image_recipe)!!.constantState

                if (currentRecipeDrawable != null && currentRecipeDrawable.constantState == defaultRecipeDrawable) {
                    // Current drawable is the default image, proceed to load
                    Helper.loadRecipeImage(binding.imageView, item.imgSource)
                }
                // Current drawable is not the default image, do not load

            }

            // Load Author img
            if (item.createdBy.imgSource.isNotEmpty()) {
                val currentAuthorDrawable = binding.imgAuthorIV.drawable
                val defaultAuthorDrawable = ContextCompat.getDrawable(context, R.drawable.default_image_recipe)


                if ((currentAuthorDrawable != null) && currentAuthorDrawable.constantState == defaultAuthorDrawable?.constantState) {
                    // Current drawable is the default image, proceed to load
                    loadUserImage(binding.imgAuthorIV, item.createdBy.imgSource)
                }
                // Current drawable is not the default image, do not load

            }

            /**
             * Details
             */

            //------- AUTOR DA RECIPE -------

            binding.nameAuthorTV.text = item.createdBy.name

            if (!item.createdBy.verified) {
                binding.verifyUserIV.visibility = View.INVISIBLE
            }

            //AUTHOR-> IMG
            if (item.createdBy.imgSource.contains("avatar")){
                val avatar= Avatar.getAvatarByName(item.createdBy.imgSource)
                binding.imgAuthorIV.setImageResource(avatar!!.imgId)

            }


            //------- INFOS DA RECIPE -------

            binding.dateTV.text = formatServerTimeToDateString(item.createdDate)
            binding.recipeTitleTV.text = item.title
            binding.recipeDescriptionTV.text = item.description
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }
            binding.nLikeTV.text = item.likes.toString()



            //------- RECEITA VERIFICADA OU NÃO -------
            if (!item.verified){
                binding.verifyRecipeIV.visibility= View.INVISIBLE
                binding.verifyRecipeTV.visibility= View.INVISIBLE
            }else{
                binding.verifyRecipeIV.visibility= View.VISIBLE
                binding.verifyRecipeTV.visibility= View.VISIBLE
            }


            binding.ratingRecipeRB.rating = item.sourceRating.toFloat()
            binding.ratingMedTV.text = item.sourceRating


            /**
             * Likes Function
             */

            if (item.liked)
                binding.likeIB.setImageResource(R.drawable.ic_like_active)
            else
                binding.likeIB.setImageResource(R.drawable.ic_like_black)


            binding.likeIB.setOnClickListener {
                if(item.liked)
                    onLikeClicked.invoke(item, false)
                else
                    onLikeClicked.invoke(item, true)

            }

            /**
             * Saves Function
             */

            if(item.saved)
                binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
            else
                binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

            binding.favoritesIB.setOnClickListener {
                if(item.saved)
                    onSaveClicked.invoke(item, false)
                else
                    onSaveClicked.invoke(item, true)

            }
        }
    }
}
