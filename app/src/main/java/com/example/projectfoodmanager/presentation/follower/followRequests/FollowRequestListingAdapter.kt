package com.example.projectfoodmanager.presentation.follower.followRequests

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowerRequest
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.toRecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemFollowerLayoutBinding
import com.example.projectfoodmanager.util.BaseAdapter
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage


class FollowRequestListingAdapter(
    val onItemClicked: (Int) -> Unit,
    val onAcceptClicked: (Int, Int) -> Unit,
    val onSendRequestClicked: (Int, Int) -> Unit,
    val onCancelRequestClicked: (Int, Int) -> Unit,
    val onRemoveBTNClicked: (Int, Int) -> Unit,
) : BaseAdapter<FollowerRequest, ItemFollowerLayoutBinding>(
    ItemFollowerLayoutBinding::inflate
) {

    private val TAG: String = "FollowerAdapter"

    fun updateItemRequestSent(id: Int,state: Boolean) {
        for ((index, item) in getItems().withIndex()){
            if (item.follower.id == id) {
                val followerRequest = getItems()[index]
                followerRequest.requestSent = state
                updateItem(index, followerRequest)
                break
            }
        }
    }

    fun updateItemFollower(id: Int,state: Boolean) {
        for ((index, item) in getItems().withIndex()){
            if (item.id == id) {
                val followerRequest = getItems()[index]
                followerRequest.isFollow = state
                updateItem(index, followerRequest)
                break
            }
        }
    }



    override fun bind(binding: ItemFollowerLayoutBinding, item: FollowerRequest, position: Int) {

        loadUserImage(binding.imgAuthorIV, item.follower.imgSource)



        binding.nameTV.text= Helper.formatNameToNameUpper(item.follower.name)
        binding.usernameTV.text= Helper.formatNameToNameUpper(item.follower.username)

        binding.verifyUserIV.visibility = if (item.follower.verified) View.VISIBLE else View.INVISIBLE



        binding.itemLayoutCL.setOnClickListener {
            onItemClicked.invoke(item.follower.id)
        }


        if (!item.isFollow){
            binding.acceptFollowRequest.visibility=View.VISIBLE
        }
        else{
            binding.acceptFollowRequest.visibility=View.GONE
        }

        binding.acceptFollowRequest.setOnClickListener {
            onAcceptClicked.invoke(position,item.id)
        }

        // Follow Request

        if (item.requestSent) {
            binding.removeFollowRequestBTN.visibility=View.VISIBLE
            binding.sendFollowRequestBTN.visibility=View.GONE
        }
        else{
            binding.removeFollowRequestBTN.visibility=View.GONE
            binding.sendFollowRequestBTN.visibility=View.VISIBLE
        }

        binding.sendFollowRequestBTN.setOnClickListener {
            onSendRequestClicked.invoke(position,item.follower.id)
        }

        binding.removeFollowRequestBTN.setOnClickListener {
            onCancelRequestClicked.invoke(position,item.follower.id)
        }
    }


}
