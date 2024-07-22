package com.example.projectfoodmanager.presentation.notification

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.notification.Notification
import com.example.projectfoodmanager.data.model.user.UserSimplified
import com.example.projectfoodmanager.databinding.NotificationListEntryBinding
import com.example.projectfoodmanager.util.Helper.Companion.getRelativeTime
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.show


class NotificationListingAdapter(
    val context: Context?,
    val onItemClick: ( Notification) -> Unit,
    val onAuthorClick: (UserSimplified) -> Unit
) : RecyclerView.Adapter<NotificationListingAdapter.MyViewHolder>() {

    private val TAG: String = "NotificationListingAdapter"
    private val space: String = "  "
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
        notifyItemRangeChanged(0,this.list.size)
    }


    fun addItem(item: Notification, position: Int =list.size) {
        list.add(index = position, element = item)
        notifyItemInserted(position)
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

    fun removeItem(position: Int): Notification {

        return list.removeAt(position).also {
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(private val binding: NotificationListEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notification) {
            val createdDateTreated = getRelativeTime(item.created_date)

            val spannableString = SpannableStringBuilder()
                .append(item.message)
                .append(space) // Append space character
                .append(createdDateTreated)

            // Set font size for title
            spannableString.setSpan(
                AbsoluteSizeSpan(14, true),
                0,
                item.message.length,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Set smaller font size for created date
            spannableString.setSpan(
                AbsoluteSizeSpan(8, true),
                item.message.length + space.length, // Adjust the start index to include space
                spannableString.length,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Set grey color for created date
            spannableString.setSpan(
                ForegroundColorSpan(Color.GRAY),
                item.message.length + space.length, // Adjust the start index to include space
                spannableString.length,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.notificationTV.text = spannableString

            loadUserImage(binding.imgAuthorIV, item.from_user.imgSource)

            item.recipe?.let{
                binding.imgObjectCV.show()
                loadRecipeImage(binding.imgObjectIV, it.imgSource)
            }

            item.comment?.let{
                binding.imgObjectCV.show()
                loadRecipeImage(binding.imgObjectIV, it.recipe.imgSource)
            }

            binding.notificationCV.setOnClickListener { onItemClick.invoke(item) }
            binding.imgAutorCV.setOnClickListener { onAuthorClick.invoke(item.from_user) }


        }
}}