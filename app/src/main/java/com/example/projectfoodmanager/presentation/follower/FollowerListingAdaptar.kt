package com.example.projectfoodmanager.presentation.follower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemFollowerLayoutBinding
import com.example.projectfoodmanager.util.FireStorage
import com.example.projectfoodmanager.util.FollowType
import com.example.projectfoodmanager.util.Helper
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class FollowerListingAdaptar(
    followType: Int,
    val onItemClicked: (Int) -> Unit,
    val onActionBTNClicked: (Int) -> Unit,
    val onRemoveBTNClicked: (Int,Int) -> Unit,
) : RecyclerView.Adapter<FollowerListingAdaptar.MyViewHolder>() {

    private val TAG: String? = "FollowerAdapter"
    private var list: MutableList<User> = arrayListOf()
    private val followType: Int = followType


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemFollowerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<User>){
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int,item: User){
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


    inner class MyViewHolder(private val binding: ItemFollowerLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {

            //Load Author img
            Helper.loadUserImage(binding.imgAuthorIV, user.img_source)

            binding.nameTV.text= Helper.formatNameToNameUpper(user.name)

            if(user.verified){
                binding.verifyUserIV.visibility=View.VISIBLE
            }else{
                binding.verifyUserIV.visibility=View.INVISIBLE
            }

            when(followType){
                FollowType.NOT_FOLLOWER ->{
                    binding.removeFollowBTN.visibility=View.VISIBLE
                    binding.removeFollowBTN.text="Remove"
                    binding.actionFollowBTN.text="Confirmation"
                }
                FollowType.FOLLOWERS -> {
                    binding.removeFollowBTN.visibility=View.VISIBLE
                    binding.removeFollowBTN.text="Remove"
                    binding.actionFollowBTN.visibility=View.GONE
                }
                FollowType.FOLLOWEDS -> {
                    binding.removeFollowBTN.visibility=View.VISIBLE
                    binding.removeFollowBTN.text="Followed"
                    binding.actionFollowBTN.visibility=View.GONE
                }
            }

            binding.itemLayoutCL.setOnClickListener {
                onItemClicked.invoke(user.id)
            }

            binding.actionFollowBTN.setOnClickListener {

                if (binding.removeFollowBTN.visibility==View.VISIBLE)
                    binding.removeFollowBTN.visibility=View.GONE

                onActionBTNClicked.invoke(user.id)
            }

            binding.removeFollowBTN.setOnClickListener {
                binding.removeFollowBTN.visibility=View.GONE
                binding.actionFollowBTN.text="Follow"
                onRemoveBTNClicked.invoke(bindingAdapterPosition, user.id)
            }

        }


    }




}
