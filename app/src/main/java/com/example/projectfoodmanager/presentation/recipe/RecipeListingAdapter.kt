package com.example.projectfoodmanager.presentation.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.recipe.toRecipeSimplified
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener

class RecipeListingAdapter(
    val onItemClicked: (Int, RecipeSimplified) -> Unit,
    val onLikeClicked: (RecipeSimplified, Boolean) -> Unit,
    val onSaveClicked: (RecipeSimplified, Boolean) -> Unit,
    private val imageLoadingListener: ImageLoadingListener
) : RecyclerView.Adapter<RecipeListingAdapter.MyViewHolder>() {



    private val TAG: String = "RecipeListingAdapter"
    private var list: MutableList<RecipeSimplified> = arrayListOf()

    var imagesToLoad: Int = 4
    var imagesLoaded: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemRecipeLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<RecipeSimplified>){
        val limit = if(list.size > this.list.size) list.size else this.list.size
        this.list = list
        imagesLoaded = 0
        notifyItemRangeChanged(0,limit)
    }

    fun getList():MutableList<RecipeSimplified>{
        return this.list.toMutableList()
    }

    fun cleanList(){
        val listSize = this.list.size
        this.list = arrayListOf()
        imagesLoaded = 0
        notifyItemRangeRemoved(0,listSize)
    }

    fun setList(_list: MutableList<RecipeSimplified>){
        cleanList()
        this.list = _list
        notifyItemRangeChanged(0,this.list.size)
    }

    fun appendList(_list: MutableList<RecipeSimplified>){
        val listSize = this.list.size
        this.list = (this.list + _list).toMutableList()
        notifyItemRangeChanged(listSize,this.list.size)
    }

    fun updateItem(position: Int,item: RecipeSimplified){
        list.removeAt(position)
        list.add(position,item)
        notifyItemChanged(position)
    }

    fun updateItem(item: RecipeSimplified){
        for ((index, recipe) in list.withIndex()){
            if (recipe.id == item.id) {
                list[index] = item
                notifyItemChanged(index)
                break
            }
        }
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

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(private val binding: ItemRecipeLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecipeSimplified) {


            /**
             * Loading Images
             */

            // Load Recipe img
            if (item.imgSource.isNotEmpty())
                loadRecipeImage(binding.imageView, item.imgSource){
                    imageLoadingListener.onImageLoaded()
                }
            else
                imageLoadingListener.onImageLoaded()

            // Load Author img
            if (item.createdBy.imgSource.isNotEmpty())
                loadUserImage(binding.imgAuthorIV, item.createdBy.imgSource){
                    imageLoadingListener.onImageLoaded()
                }
            else
                imageLoadingListener.onImageLoaded()

            /**
             * Details
             */

            //Load Author name
            binding.nameAuthorTV.text = formatNameToNameUpper(item.createdBy.name)

            //Validate that the author is verified
            if (item.createdBy.verified){
                binding.verifyUserIV.visibility=View.VISIBLE
            }else{
                binding.verifyUserIV.visibility=View.INVISIBLE
            }

            binding.dateTV.text = formatServerTimeToDateString(item.createdDate)
            binding.recipeTitleTV.text = item.title
            binding.idTV.text = item.id.toString()

            // string -> localTimeDate
            binding.recipeDescriptionTV.text = item.description
            binding.itemLayout.setOnClickListener {

                onItemClicked.invoke(adapterPosition, item)
            }
            binding.nLikeTV.text = item.likes.toString()

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

            if(item.liked)
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
