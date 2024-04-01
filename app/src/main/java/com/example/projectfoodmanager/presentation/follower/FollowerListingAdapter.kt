package com.example.projectfoodmanager.presentation.follower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.ItemFollowerLayoutBinding
import com.example.projectfoodmanager.util.FollowType
import com.example.projectfoodmanager.util.Helper


class FollowerListingAdapter(
    private var followType: Int,
    val onItemClicked: (Int) -> Unit,
    val onActionBTNClicked: (Int,Int) -> Unit,
    val onRemoveBTNClicked: (Int,Int) -> Unit,
) : RecyclerView.Adapter<FollowerListingAdapter.MyViewHolder>() {

    private val TAG: String = "FollowerAdapter"
    private var list: MutableList<User> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemFollowerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<User>, followType: Int? = null){
        if (followType != null)
            this.followType = followType
        this.list = list
        notifyDataSetChanged()
    }

    fun getList():MutableList<User>{
        return this.list
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
            Helper.loadUserImage(binding.imgAuthorIV, user.imgSource)

            binding.nameTV.text= Helper.formatNameToNameUpper(user.name)

            if(user.verified){
                binding.verifyUserIV.visibility=View.VISIBLE
            }else{
                binding.verifyUserIV.visibility=View.INVISIBLE
            }

            when(followType){
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
                else -> {
                    binding.removeFollowBTN.visibility=View.GONE
                    binding.actionFollowBTN.visibility=View.VISIBLE
                }
            }

            binding.itemLayoutCL.setOnClickListener {
                onItemClicked.invoke(user.id)
            }

            binding.actionFollowBTN.setOnClickListener {

                onActionBTNClicked.invoke(bindingAdapterPosition,user.id)
            }

            binding.removeFollowBTN.setOnClickListener {

                onRemoveBTNClicked.invoke(bindingAdapterPosition, user.id)
            }

        }


    }




}
