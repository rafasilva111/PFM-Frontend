package com.example.projectfoodmanager.presentation.shoppingList.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.databinding.ItemShoppingListEntryBinding


class ShoppingListsListingAdapter(
    val context: Context?,
    val onItemClicked: (Int, ShoppingList) -> Unit,
    val onItemEdit: (Int, ShoppingList) -> Unit,
    val onItemArchive: (Int, ShoppingList) -> Unit,
    val onItemDelete: (Int, ShoppingList) -> Unit,
) : RecyclerView.Adapter<ShoppingListsListingAdapter.MyViewHolder>() {

    private val TAG: String = "ShoppingListListingAdapter"
    var list: MutableList<ShoppingList> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemShoppingListEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<ShoppingList>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, item: ShoppingList) {
        list.removeAt(position)
        list.add(position, item)
        notifyItemChanged(position)
    }


    fun cleanList() {
        this.list = arrayListOf()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(private val binding: ItemShoppingListEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingList) {
            binding.nameTV.text = item.name
            binding.createdDate.text = item.createdDate

            binding.shoppingListLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }

            binding.btnEdit.setOnClickListener { onItemEdit.invoke(adapterPosition, item) }
            binding.btnArchive.setOnClickListener { onItemArchive.invoke(adapterPosition, item) }
            binding.btnDelete.setOnClickListener { onItemDelete.invoke(adapterPosition, item) }

        }
    }
}