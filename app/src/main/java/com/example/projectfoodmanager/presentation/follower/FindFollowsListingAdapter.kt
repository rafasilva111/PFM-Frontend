package com.example.projectfoodmanager.presentation.follower

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.follows.UserToFollow
import com.example.projectfoodmanager.databinding.ItemFollowerLayoutBinding
import com.example.projectfoodmanager.util.BaseAdapter
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener


class FindFollowsListingAdapter(
    val onItemClicked: (Int) -> Unit,
    val onActionBTNClicked: (Int,Int) -> Unit,
    val onRemoveFollowRequestBTN: (Int,Int) -> Unit,
    private val imageLoadingListener: ImageLoadingListener
) : BaseAdapter<UserToFollow, ItemFollowerLayoutBinding>(
    ItemFollowerLayoutBinding::inflate
) {

    private val TAG: String = "FollowerAdapter"

    fun updateItem(position: Int,state: Boolean = true){
        val item = this.getItems().removeAt(position)
        item.requestSent = state
        this.updateItem(position,item)
    }

    override fun bind(binding: ItemFollowerLayoutBinding, item: UserToFollow, position: Int) {

        if (item.user.name == "k")
            println()

        //Load Author img
        loadUserImage(binding.imgAuthorIV, item.user.imgSource) {
            imageLoadingListener.onImageLoaded()
        }

        binding.nameTV.text= formatNameToNameUpper(item.user.name)
        binding.usernameTV.text= formatNameToNameUpper(item.user.username)

        if(item.user.verified){
            binding.verifyUserIV.visibility=View.VISIBLE
        }else{
            binding.verifyUserIV.visibility=View.INVISIBLE
        }

        if (item.requestSent) {
            binding.removeFollowRequestBTN.visibility=View.VISIBLE
            binding.sendFollowRequestBTN.visibility=View.GONE
        }
        else{
            binding.removeFollowRequestBTN.visibility=View.GONE
            binding.sendFollowRequestBTN.visibility=View.VISIBLE
        }


        binding.itemLayoutCL.setOnClickListener {
            onItemClicked.invoke(item.user.id)
        }

        binding.sendFollowRequestBTN.setOnClickListener {
            onActionBTNClicked.invoke(position,item.user.id)
        }

        binding.removeFollowRequestBTN.setOnClickListener {

            onRemoveFollowRequestBTN.invoke(position, item.user.id)
        }

    }


}
