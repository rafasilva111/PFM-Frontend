package com.example.projectfoodmanager.presentation.recipe.comments;

import android.content.Context
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.ItemCommentLayoutBinding
import com.example.projectfoodmanager.util.FireStorage
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.SharedPreference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.security.AccessController.getContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CommentsListingAdapter(
    val sharedPreferences: SharedPreference,
    val onProfilePressed: (User) -> Unit,
    val onLikePressed: (View,Int) -> Unit,
): RecyclerView.Adapter<CommentsListingAdapter.MyViewHolder>() {

    private lateinit var  userSession: User
    private var i : Int = 0
    private val TAG: String? = "RecipeListingAdapter"
    private var list: MutableList<Comment> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemCommentLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        userSession  = sharedPreferences.getUserSession()
        return MyViewHolder(itemView,parent.context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        holder.bind(item, userSession.id== item.user!!.id)


    }

    fun updateList(list: MutableList<Comment>){
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int,item: Comment){
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


    inner class MyViewHolder(private val binding: ItemCommentLayoutBinding,private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment, owner: Boolean) {
            binding.CLComment.visibility = if (owner) View.GONE else View.VISIBLE
            binding.CLCommentOwner.visibility = if (owner) View.VISIBLE else View.GONE

            val user = item.user
            val authorText = context.getString(R.string.full_name, user?.name)
            val messageText = item.text

            val dataText = if (item.updated_date != item.created_date) {
                getRelativeTime(item.updated_date!!)
            } else {
                getRelativeTime(item.created_date!!)
            }

            if (owner) {
                binding.TVCAuthorOwner.text = authorText
                binding.TVCMessageOwner.text = messageText
                binding.TVCDataOwner.text = dataText
            } else {
                binding.TVCAuthor.text = authorText
                binding.TVCMessage.text = messageText
                binding.TVCData.text = dataText
            }

            binding.IVAuthor.setOnClickListener {
                item.user?.let { it1 -> onProfilePressed.invoke(it1) }
            }

            binding.CVComment.setOnClickListener {
                i++
                val handler = Handler()
                handler.postDelayed({
                    if (i == 2) {
                        binding.CVComment.isClickable = false
                        onLikePressed.invoke(binding.CVComment,item.id)
                    }
                    i = 0
                }, 500)
            }




            if (owner) {
                Helper.loadUserImage(binding.IVAuthorOwner, userSession.img_source)
            }
            else{
                item.user!!.img_source.let{
                    Helper.loadUserImage(binding.IVAuthor, it)}
            }
        }
    }


    private fun formatDate(date: String): String? {

        try {
            // Parse the time string into a LocalDateTime object
            val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
            val time = ZonedDateTime.parse(date, inputFormat)

            // Format the LocalDateTime object into a string
            val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedTime = time.format(outputFormat)

            // Print the formatted time string
            return formattedTime
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getRelativeTime(timeString: String): String? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
            val currentDateTime = LocalDateTime.now().atZone(ZoneId.of("Europe/Lisbon"))
            val messageDateTime = LocalDateTime.parse(timeString, formatter)
            val duration = Duration.between(messageDateTime, currentDateTime)


            return when {
                duration.seconds < 60 -> "Just now"
                duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
                duration.toHours() < 24 -> "${duration.toHours()} hours ago"
                duration.toHours() < 48 -> "Yesterday"
                duration.toDays() < 10 -> "${duration.toDays()} days ago"
                else -> {
                    formatDate(timeString)
                }
            }
        } catch (e: DateTimeParseException) {
            "Invalid time format"
        }
        return null
    }
}




   /* :BaseAdapter() {

    private val items: List<Comment> = listOf()


    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        var vh: ViewHolderComments


        if (convertView == null) {
            if (items[position].user!!.id == clientId ){
                val layoutInflater = LayoutInflater.from(context)

                view = layoutInflater.inflate(R.layout.item_comment_layout_owner, parent, false)
                vh = ViewHolderComments(view)
                view.tag = vh

            }
            else{
                val layoutInflater = LayoutInflater.from(context)

                view = layoutInflater.inflate(R.layout.item_comment_layout, parent, false)
                vh = ViewHolderComments(view)
                view.tag = vh

            }

        } else {
            view = convertView
        }
        vh = view.tag as ViewHolderComments

        vh.tvName.text = items[position].user!!.first_name+items[position].user!!.last_name

        if (items[position].updated_date !=items[position].created_date){
            // created date
            // TODO Rui pôr icon a avisar que mensagem já foi alterada ( tipo um lapis)


            vh.tvData.text = getRelativeTime(items[position].updated_date!!)
        }
        else{
            vh.tvData.text = getRelativeTime(items[position].created_date!!)

        }

        vh.tvMessage.text = items[position].text

        return view
    }




}



private class ViewHolderComments(view: View?) {
    val tvName: TextView = view?.findViewById<TextView>(R.id.TV_c_Author) as TextView
    val tvData: TextView = view?.findViewById<TextView>(R.id.TV_c_Data) as TextView
    val tvMessage: TextView = view?.findViewById<TextView>(R.id.TV_c_Message) as TextView
}*/