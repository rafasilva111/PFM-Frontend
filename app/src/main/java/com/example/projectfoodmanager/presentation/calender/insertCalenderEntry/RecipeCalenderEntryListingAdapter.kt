package com.example.projectfoodmanager.presentation.calender.insertCalenderEntry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.SharedPreference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class RecipeCalenderEntryListingAdapter(
    val onItemClicked: (Int, Recipe) -> Unit
) : RecyclerView.Adapter<RecipeCalenderEntryListingAdapter.MyViewHolder>() {

    @Inject
    lateinit var sharedPreference: SharedPreference

    private var user: User? = null
    private val TAG: String = "RecipeListingAdapter"
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

            if (user==null)
                user= sharedPreference.getUserSession()

            binding.dateTV.text = formatServerTimeToDateString(item.created_date)

            //Load Recipe img
            Helper.loadRecipeImage(binding.imageView,item.img_source)


            binding.recipeTitleTV.text = item.title
            binding.recipeDescriptionTV.text = item.description.toString()
            binding.itemLayout.setOnClickListener {
                onItemClicked.invoke(adapterPosition, item)
            }
            binding.nLikeTV.text = item.likes.toString()
            binding.ratingRecipeRB.rating = item.source_rating.toFloat()
            binding.ratingMedTV.text = item.source_rating.toString()

        /*    binding.timeTV.text = item.time
            binding.difficultyTV.text = item.difficulty
            binding.portionTV.text = item.portion*/

            binding.favoritesIB.visibility=View.INVISIBLE
            //--------- LIKES ---------

            if(user!!.checkIfLiked(item) != -1){
                binding.likeIB.setImageResource(R.drawable.ic_like_active)
            }
            else
                binding.likeIB.setImageResource(R.drawable.ic_like_black)


        }
    }
}
