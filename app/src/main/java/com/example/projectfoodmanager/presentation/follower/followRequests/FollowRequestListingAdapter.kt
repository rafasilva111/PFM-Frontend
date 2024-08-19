package com.example.projectfoodmanager.presentation.follower.followRequests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowerRequest
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemFollowerLayoutBinding
import com.example.projectfoodmanager.databinding.ItemFollowerRequestLayoutBinding
import com.example.projectfoodmanager.util.BaseAdapter
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage


class FollowRequestListingAdapter(
    val onItemClicked: (Int) -> Unit,
    val onActionBTNClicked: (Int,Int) -> Unit,
) : BaseAdapter<FollowerRequest, ItemFollowerLayoutBinding>(
    ItemFollowerLayoutBinding::inflate
) {

    private val TAG: String = "FollowerAdapter"
    private var list: MutableList<User> = arrayListOf()



    fun updateItem(position: Int,item: User){
        list.removeAt(position)
        list.add(position,item)
        notifyItemChanged(position)
    }




    inner class MyViewHolder(private val binding: ItemFollowerRequestLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {

            //Load Author img
            loadUserImage(binding.imgAuthorIV, user.imgSource)

            binding.nameTV.text= Helper.formatNameToNameUpper(user.name)

            if(user.verified){
                binding.verifyUserIV.visibility=View.VISIBLE
            }else{
                binding.verifyUserIV.visibility=View.INVISIBLE
            }



            binding.itemLayoutCL.setOnClickListener {
                onItemClicked.invoke(user.id)
            }

            binding.actionFollowBTN.setOnClickListener {

                onActionBTNClicked.invoke(bindingAdapterPosition,user.id)
            }
        }


    }

    override fun bind(binding: ItemFollowerLayoutBinding, item: FollowerRequest, position: Int) {
        //Load Author img
        loadUserImage(binding.imgAuthorIV, item.follower.imgSource)

        binding.nameTV.text= Helper.formatNameToNameUpper(item.follower.name)

        if(item.follower.verified){
            binding.verifyUserIV.visibility=View.VISIBLE
        }else{
            binding.verifyUserIV.visibility=View.INVISIBLE
        }



        binding.itemLayoutCL.setOnClickListener {
            onItemClicked.invoke(item.follower.id)
        }

        binding.actionFollowBTN.setOnClickListener {

            onActionBTNClicked.invoke(position,item.follower.id)
        }
    }


}
