package com.example.projectfoodmanager.presentation.recipe.comments;

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.ItemCommentLayoutBinding
import com.example.projectfoodmanager.util.FireStorage
import com.example.projectfoodmanager.util.SharedPreference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class CommentsListingAdapter(
    val sharedPreferences: SharedPreference
): RecyclerView.Adapter<CommentsListingAdapter.MyViewHolder>() {

    private var userSession: User? = null
    private var i : Int = 0
    private val TAG: String? = "RecipeListingAdapter"
    private var list: MutableList<Comment> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemCommentLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(itemView,parent.context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        holder.bind(item,sharedPreferences.getUserSession()!!.id== item.user!!.id)


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


    inner class MyViewHolder(private val binding: ItemCommentLayoutBinding,
                             private val context: Context
                             ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment, owner: Boolean) {
            if (owner){
                binding.CLComment.visibility = View.GONE
                binding.CLCommentOwner.visibility = View.VISIBLE
                binding.TVCAuthorOwner.text = context.getString(R.string.full_name, item.user!!.name)
                binding.TVCMessageOwner.text = item.text

                if (item.updated_date !=item.created_date){
                    // created date
                    // TODO Rui pôr icon a avisar que mensagem já foi alterada ( tipo um lapis)
                    binding.TVCDataOwner.text = getRelativeTime(item.updated_date!!)
                }
                else{
                    binding.TVCDataOwner.text = getRelativeTime(item.created_date!!)

                }

                binding.CVComment.setOnClickListener {
                    i++
                    Toast.makeText(context, "Long clicked", Toast.LENGTH_LONG).show()
                    val handler = Handler()

                    handler.post {
                        if (i == 2) {
                            //onItemClicked.invoke(adapterPosition, item) }
                            Toast.makeText(context, "Double clicked", Toast.LENGTH_LONG).show()
                        }
                        i = 0
                    }
                }


                if (userSession==null)
                    userSession = sharedPreferences.getUserSession()


                if (userSession!!.img_source.contains("avatar")){
                    val avatar= Avatar.getAvatarByName(userSession!!.img_source)
                    binding.IVAuthorOwner.setImageResource(avatar!!.imgId)

                }else{
                    val imgRef = Firebase.storage.reference.child("${FireStorage.user_profile_images}${userSession!!.img_source}")
                    imgRef.downloadUrl.addOnSuccessListener { Uri ->
                        Glide.with(binding.IVAuthorOwner.context).load(Uri.toString()).into(binding.IVAuthorOwner)
                    }
                        .addOnFailureListener {
                            Glide.with(binding.IVAuthorOwner.context)
                                .load(R.drawable.good_food_display___nci_visuals_online)
                                .into(binding.IVAuthorOwner)
                        }
                }

//            binding.CVComment.setOnLongClickListener { true
//                // TODO open profile
//                Toast.makeText(context, "Long clicked", Toast.LENGTH_LONG).show()
//            }

            }
            else{
                binding.CLComment.visibility = View.VISIBLE
                binding.CLCommentOwner.visibility = View.GONE
                binding.TVCAuthor.text = context.getString(R.string.full_name, item.user!!.name)
                binding.TVCMessage.text = item.text
                if (item.updated_date !=item.created_date){
                    // created date
                    // TODO Rui pôr icon a avisar que mensagem já foi alterada ( tipo um lapis)
                    binding.TVCData.text = getRelativeTime(item.updated_date!!)
                }
                else{
                    binding.TVCData.text = getRelativeTime(item.created_date!!)

                }

                binding.CVComment.setOnClickListener {
                    i++
                    Toast.makeText(context, "Long clicked", Toast.LENGTH_LONG).show()
                    val handler = Handler()

                    handler.post {
                        if (i == 2) {
                            //onItemClicked.invoke(adapterPosition, item) }
                            Toast.makeText(context, "Double clicked", Toast.LENGTH_LONG).show()
                        }
                        i = 0
                    }
                }

                if (item.user.img_source != "") {
                    val imgRef = Firebase.storage.reference.child("${FireStorage.user_profile_images}${item.user.img_source}")
                    imgRef.downloadUrl.addOnSuccessListener { Uri ->
                        val imageURL = Uri.toString()
                        Glide.with(binding.IVAuthor.context).load(imageURL)
                            .into(binding.IVAuthor)
                    }
                }

//            binding.CVComment.setOnLongClickListener { true
//                // TODO open profile
//                Toast.makeText(context, "Long clicked", Toast.LENGTH_LONG).show()
//            }

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