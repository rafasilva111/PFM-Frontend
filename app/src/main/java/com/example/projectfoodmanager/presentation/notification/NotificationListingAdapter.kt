package com.example.projectfoodmanager.presentation.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.notifications.Notification
import com.example.projectfoodmanager.databinding.NotificationListEntryBinding


class NotificationListingAdapter(
    val context: Context?,
    val onItemClick: (Int, Notification) -> Unit
) : RecyclerView.Adapter<NotificationListingAdapter.MyViewHolder>() {

    private val TAG: String = "NotificationListingAdapter"
    var list: MutableList<Notification> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = NotificationListEntryBinding.inflate(
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

    fun updateList(list: MutableList<Notification>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, item: Notification) {
        list.removeAt(position)
        list.add(position, item)
        notifyItemChanged(position)
    }


    fun cleanList() {
        val oldSize = list.size
        list.clear()

        // Use more specific change events if you know the range of changes
        if (oldSize > 0) {
            notifyItemRangeRemoved(0, oldSize)
        }
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(private val binding: NotificationListEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notification) {

            binding.notificationTV.text = item.title

            // todo rui por aqui uma data
            //binding.notificationTV.text = item.created_date

            // ir com a notification
            binding.notificationLA.setOnClickListener { onItemClick.invoke(adapterPosition, item) }


        }
    }
}