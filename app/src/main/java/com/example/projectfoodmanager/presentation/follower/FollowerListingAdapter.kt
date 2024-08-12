package com.example.projectfoodmanager.presentation.follower


import android.view.View
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.user.UserSimplified
import com.example.projectfoodmanager.databinding.ItemFollowerLayoutBinding
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.util.BaseAdapter
import com.example.projectfoodmanager.util.Helper


class FollowerListingAdapter(
    val onItemClicked: (Int) -> Unit,
    val onActionBTNClicked: (Int,Int) -> Unit,
    val onRemoveBTNClicked: (Int,Int) -> Unit,
) : BaseAdapter<UserSimplified, ItemFollowerLayoutBinding>(
    ItemFollowerLayoutBinding::inflate
) {

    private val TAG: String = "FollowerAdapter"
    private var list: MutableList<User> = arrayListOf()
    private lateinit var followType: String

    fun updateList(list: MutableList<User>, followType:String){
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


    override fun bind(binding: ItemFollowerLayoutBinding, item: UserSimplified, position: Int) {
        //Load Author img
        Helper.loadUserImage(binding.imgAuthorIV, item.imgSource)

        binding.nameTV.text= Helper.formatNameToNameUpper(item.name)

        if(item.verified){
            binding.verifyUserIV.visibility=View.VISIBLE
        }else{
            binding.verifyUserIV.visibility=View.INVISIBLE
        }

        when(followType){
            FollowerFragment.Companion.SelectedTab.FOLLOWERS -> {
                binding.removeFollowBTN.visibility=View.VISIBLE
                binding.removeFollowBTN.text="Remove"
                binding.actionFollowBTN.visibility=View.GONE
            }
            FollowerFragment.Companion.SelectedTab.FOLLOWS -> {
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
