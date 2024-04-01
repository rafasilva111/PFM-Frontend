package com.example.projectfoodmanager.presentation.follower.followRequests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.ItemFollowerRequestLayoutBinding
import com.example.projectfoodmanager.util.Helper


class FollowRequestListingAdapter(
    val onItemClicked: (Int) -> Unit,
    val onActionBTNClicked: (Int,Int) -> Unit,
) : RecyclerView.Adapter<FollowRequestListingAdapter.MyViewHolder>() {

    private val TAG: String = "FollowerAdapter"
    private var list: MutableList<User> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemFollowerRequestLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
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


    inner class MyViewHolder(private val binding: ItemFollowerRequestLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {

            //Load Author img
            Helper.loadUserImage(binding.imgAuthorIV, user.imgSource)

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




}
