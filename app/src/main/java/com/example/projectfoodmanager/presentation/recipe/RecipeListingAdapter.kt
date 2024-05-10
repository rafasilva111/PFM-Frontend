package com.example.projectfoodmanager.presentation.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.recipe.Recipe
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.SharedPreference


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

            //Load Author img
            loadUserImage(binding.imgAuthorIV,item.createdBy.imgSource)

            //Load Author name
            binding.nameAuthorTV.text = formatNameToNameUpper(item.createdBy.name)

            //Validate that the author is verified
            if (item.createdBy.verified){
                binding.verifyUserIV.visibility=View.VISIBLE
            }else{
                binding.verifyUserIV.visibility=View.INVISIBLE
            }

            // Load Recipe img
            if (item.imgSource.isNotEmpty()){

                Helper.loadRecipeImage(binding.imageView,item.imgSource)
            }

            // Load Author img
            if (item.createdBy.imgSource.isNotEmpty()){
                //-> Load Recipe img
                loadUserImage(binding.imgAuthorIV,item.createdBy.imgSource)
            }


            binding.dateTV.text = formatServerTimeToDateString(item.createdDate)
            binding.recipeTitleTV.text = item.title
            binding.idTV.text = item.id.toString()

            // string -> localTimeDate
            binding.recipeDescriptionTV.text = item.description
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }
            binding.nLikeTV.text = item.likes.toString()

            if (!item.verified){
                binding.verifyRecipeIV.visibility= View.INVISIBLE
                binding.verifyRecipeTV.visibility= View.INVISIBLE
            }else{
                binding.verifyRecipeIV.visibility= View.VISIBLE
                binding.verifyRecipeTV.visibility= View.VISIBLE
            }

            // get user from shared prefrences
            val user = sharedPreference.getUserSession()

            binding.ratingRecipeRB.rating = item.sourceRating.toFloat()
            binding.ratingMedTV.text = item.sourceRating

/*

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
*/

            //--------- LIKES ---------
            if(user.checkIfLiked(item) != -1){
                binding.likeIB.setImageResource(R.drawable.ic_like_active)
            }
            else
                binding.likeIB.setImageResource(R.drawable.ic_like_black)


            binding.likeIB.setOnClickListener {
                if(user.checkIfLiked(item) == -1) {
                    onLikeClicked.invoke(item, true)
                }
                else
                {
                    onLikeClicked.invoke(item, false)
                }
            }


            //--------- FAVORITES ---------
            if(user.checkIfSaved(item) != -1){
                binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
            }
            else
                binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

            binding.favoritesIB.setOnClickListener {
                if(user.checkIfSaved(item) == -1) {
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
