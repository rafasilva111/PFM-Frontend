package com.example.projectfoodmanager.presentation.follower


import android.view.View
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemFollowerLayoutBinding
import com.example.projectfoodmanager.util.BaseAdapter
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener


class FollowerListingAdapter(
    val onItemClicked: (Int) -> Unit,
    val onActionBTNClicked: (Int,Int) -> Unit,
    val onRemoveBTNClicked: (Int,Int) -> Unit,
    private val imageLoadingListener: ImageLoadingListener
) : BaseAdapter<User, ItemFollowerLayoutBinding>(
    ItemFollowerLayoutBinding::inflate
) {

    private val TAG: String = "FollowerAdapter"
    private lateinit var followType: String



    fun setItems(list: MutableList<User>, followType:String){
        this.followType = followType
        this.setItems(list)
    }

    override fun bind(binding: ItemFollowerLayoutBinding, item: User, position: Int) {

        //Load Author img
        loadUserImage(binding.imgAuthorIV, item.imgSource) {
            if (position == 0)
                imageLoadingListener.onImageLoaded()
        }


        binding.nameTV.text= Helper.formatNameToNameUpper(item.name)

        if(item.verified){
            binding.verifyUserIV.visibility=View.VISIBLE
        }else{
            binding.verifyUserIV.visibility=View.INVISIBLE
        }

        when(followType){
            FollowerFragment.Companion.SelectedTab.FOLLOWERS -> {
                binding.removeFollowBTN.visibility=View.VISIBLE
                binding.removeFollowBTN.text=binding.root.context.getString(R.string.COMMON_REMOVE)
                binding.actionFollowBTN.visibility=View.GONE
            }
            FollowerFragment.Companion.SelectedTab.FOLLOWS -> {
                binding.removeFollowBTN.visibility=View.VISIBLE
                binding.removeFollowBTN.text=binding.root.context.getString(R.string.COMMON_FOLLOWED)
                binding.actionFollowBTN.visibility=View.GONE
            }
            else -> {
                binding.removeFollowBTN.visibility=View.GONE
                binding.actionFollowBTN.visibility=View.VISIBLE
            }
        }

        binding.itemLayoutCL.setOnClickListener {
            onItemClicked.invoke(item.id)
        }

        binding.actionFollowBTN.setOnClickListener {

            onActionBTNClicked.invoke(position,item.id)
        }

        binding.removeFollowBTN.setOnClickListener {

            onRemoveBTNClicked.invoke(position, item.id)
        }


    }



}
