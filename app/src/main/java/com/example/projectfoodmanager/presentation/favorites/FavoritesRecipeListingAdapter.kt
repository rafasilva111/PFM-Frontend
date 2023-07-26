package com.example.projectfoodmanager.presentation.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.util.FireStorage
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*


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
            //binding.authorTV.text = item.company
            // todo ver com o rui
            //------- IMAGEM DA RECIPE -------
            val imgRef = Firebase.storage.reference.child(item.img_source)
            imgRef.downloadUrl.addOnSuccessListener { Uri ->
                val imageURL = Uri.toString()
                Glide.with(binding.imageView.context).load(imageURL).into(binding.imageView)
            }

            //------- AUTOR DA RECIPE -------

            // todo rafael fix esta merda
            if (item.created_by != null) {
                binding.authorTV.text = item.created_by.name

                if (!item.created_by.verified) {
                    binding.verifyUserIV.visibility = View.INVISIBLE
                }

                if (item.created_by.img_source.contains("avatar")) {
                    val avatar = Avatar.getAvatarByName(item.created_by.img_source)
                    binding.authorIV.setImageResource(avatar!!.imgId)

                } else {
                    Firebase.storage.reference.child("${FireStorage.user_profile_images}${item.created_by.img_source}").downloadUrl.addOnSuccessListener { Uri ->
                        Glide.with(binding.authorIV.context).load(Uri.toString())
                            .into(binding.authorIV)
                    }
                }
            }


            //------- INFOS DA RECIPE -------
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val date: Date? = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(item.created_date)

            binding.dateTV.text = format.format(date!!)
            binding.recipeTitleTV.text = item.title
            binding.recipeDescriptionTV.text = item.description.toString()
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

            //--------- FAVORITES ---------
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
        }
    }
}
