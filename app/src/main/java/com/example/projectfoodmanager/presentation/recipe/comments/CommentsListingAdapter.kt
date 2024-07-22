package com.example.projectfoodmanager.presentation.recipe.comments;

import android.content.Context
import android.os.Handler
import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.user.UserSimplified
import com.example.projectfoodmanager.databinding.ItemCommentLayoutBinding
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.util.Helper.Companion.getRelativeTime
class CommentsListingAdapter(
    val sharedPreferences: SharedPreference,
    val onProfilePressed: (UserSimplified) -> Unit,
    val onLikePressed: (Int, Boolean) -> Unit,
    val onDeletePressed: (Int) -> Unit,
    val onEditPressed: (Int) -> Unit,
    val onCommentPressed: (Int) -> Unit,
): RecyclerView.Adapter<CommentsListingAdapter.MyViewHolder>() {

    private lateinit var  userSession: User
    private var i : Int = 0
    private val TAG: String = "RecipeListingAdapter"
    private var list: MutableList<Comment> = arrayListOf()
    // this variable is important to keep notification comment on top
    private var pos: Int = 0


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
        notifyItemRangeChanged(pos,this.list.size)
    }
    fun cleanList(){
        val listSize = list.size
        list= arrayListOf()
        notifyItemRangeChanged(0,listSize)
    }

    private var offset = 0

    fun addFocusedItem(item: Comment) {
        addItemAtTop(item) // Add focused item at the top with current offset
        offset++ // Increment the offset

    }

    fun addItemAtTop(item: Comment) {
        list.add(offset, item)
        notifyItemInserted(offset)
    }

    fun addItemAtBottom(item: Comment) {
        list.add(item)
        notifyItemInserted(list.size)
    }

    fun removeItemById(id: Int){
        val index = list.indexOfFirst { it.id == id }

        if (index>-1){
            list.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemChanged(position)
    }

    fun updateItem(item: Comment){
        val index = list.indexOfFirst { it.id == item.id }

        if (index>-1){
            list.removeAt(index)
            list.add(index,item)
            notifyItemChanged(index)
        }

    }

    fun updateItem(position: Int,item: Comment){
        list.removeAt(position)
        list.add(position,item)
        notifyItemChanged(position)
    }


    override fun getItemCount(): Int {
        return list.size
    }




    inner class MyViewHolder(private val binding: ItemCommentLayoutBinding,private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment, owner: Boolean) {

            binding.TVCAuthor.text = context.getString(R.string.full_name,  item.user?.name)
            binding.TVCMessage.text = item.text
            binding.TVCData.text = if (item.updatedDate != item.createdDate) {
                getRelativeTime(item.updatedDate!!)
            } else {
                getRelativeTime(item.createdDate!!)
            }

            binding.TVCNLikes.text = context.getString(R.string.COMMENT_ITEM_LAYOUT_LIKES,  item.likes)
            if (item.liked)
                binding.imageView8.setBackgroundResource(R.drawable.ic_like_active)
            else
                binding.imageView8.setBackgroundResource(R.drawable.ic_like)

            binding.IVAuthor.setOnClickListener {
                item.user?.let { it1 -> onProfilePressed.invoke(it1) }
            }

            binding.CVComment.isClickable = true
            binding.CVComment.setOnClickListener {
                i++
                Handler().postDelayed({
                    if (i == 2) {
                        binding.CVComment.isClickable = false
                        onLikePressed.invoke(item.id,item.liked)
                    }
                    i = 0
                }, 500)
            }

            binding.TVCEdit.isVisible = true
            binding.TVCComment.setOnClickListener {
                onCommentPressed.invoke(item.id)
            }

            if (owner){
                binding.TVCDelete.isVisible = true
                binding.TVCDelete.setOnClickListener {
                    onDeletePressed.invoke(item.id)
                }
                binding.TVCEdit.isVisible = true
                binding.TVCEdit.setOnClickListener {
                    onEditPressed.invoke(item.id)
                }

            }

            item.user!!.imgSource.let{
                Helper.loadUserImage(binding.IVAuthor, it)}
        }
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