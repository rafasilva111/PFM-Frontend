package com.example.projectfoodmanager.presentation.follower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemFollowerLayoutBinding
import com.example.projectfoodmanager.util.FireStorage
import com.example.projectfoodmanager.util.Helper
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class FollowerListingAdaptar(followType: Int) : RecyclerView.Adapter<FollowerListingAdaptar.MyViewHolder>() {

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

            if (user.img_source.contains("avatar")){
                val avatar= Avatar.getAvatarByName(user.img_source)
                binding.imgAuthorIV.setImageResource(avatar!!.imgId)

            }else{
                val imgRef = Firebase.storage.reference.child("${FireStorage.user_profile_images}${user.img_source}")
                imgRef.downloadUrl.addOnSuccessListener { Uri ->
                    Glide.with(binding.imgAuthorIV.context).load(Uri.toString()).into(binding.imgAuthorIV)
                }
                .addOnFailureListener {
                    Glide.with(binding.imgAuthorIV.context)
                        .load(R.drawable.img_profile)
                        .into(binding.imgAuthorIV)
                }
            }


            binding.nameTV.text= Helper.formatNameToNameUpper(user.name)

            if(user.verified){
                binding.verifyUserIV.visibility=View.VISIBLE
            }else{
                binding.verifyUserIV.visibility=View.INVISIBLE
            }

        }


    }




}
