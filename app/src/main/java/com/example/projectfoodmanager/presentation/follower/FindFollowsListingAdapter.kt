package com.example.projectfoodmanager.presentation.follower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.follows.UserToFollow
import com.example.projectfoodmanager.databinding.ItemFollowerLayoutBinding
import com.example.projectfoodmanager.util.FollowType
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage


class FindFollowsListingAdapter(
    val onItemClicked: (Int) -> Unit,
    val onActionBTNClicked: (Int,Int) -> Unit,
    val onRemoveFollowRequestBTN: (Int,Int) -> Unit,
) : RecyclerView.Adapter<FindFollowsListingAdapter.MyViewHolder>() {

    private val TAG: String = "FollowerAdapter"
    private var list: MutableList<UserToFollow> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemFollowerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<UserToFollow>){
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int,state: Boolean = true){
        val item = list.removeAt(position)
        item.request_sent = state
        list.add(position,item)
        notifyItemChanged(position)
    }

    fun getList(): MutableList<UserToFollow>{
        return this.list
    }


    fun cleanList(){
        this.list= arrayListOf()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(private val binding: ItemFollowerLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userToFollow: UserToFollow) {

            //Load Author img
            loadUserImage(binding.imgAuthorIV, userToFollow.user.img_source)

            binding.nameTV.text= formatNameToNameUpper(userToFollow.user.name)

            if(userToFollow.user.verified){
                binding.verifyUserIV.visibility=View.VISIBLE
            }else{
                binding.verifyUserIV.visibility=View.INVISIBLE
            }

            if (userToFollow.request_sent) {
                binding.removeFollowRequestBTN.visibility=View.VISIBLE
                binding.actionFollowBTN.visibility=View.GONE
            }


            binding.itemLayoutCL.setOnClickListener {
                onItemClicked.invoke(userToFollow.user.id)
            }

            binding.actionFollowBTN.setOnClickListener {

                onActionBTNClicked.invoke(bindingAdapterPosition,userToFollow.user.id)
            }

            binding.removeFollowRequestBTN.setOnClickListener {

                onRemoveFollowRequestBTN.invoke(bindingAdapterPosition, userToFollow.user.id)
            }

        }


    }




}
