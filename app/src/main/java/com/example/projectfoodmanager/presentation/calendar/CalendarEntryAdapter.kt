package com.example.projectfoodmanager.presentation.calendar

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.databinding.ItemCalenderEntryBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToTimeString
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener


class CalendarEntryAdapter(
    val onItemClicked: (Int, CalenderEntry) -> Unit,
    val onDoneClicked: (Boolean, CalenderEntry) -> Unit,
    private val imageLoadingListener: ImageLoadingListener
) : RecyclerView.Adapter<CalendarEntryAdapter.MyViewHolder>() {

    private val TAG: String = "CalenderEntryAdapter"
    private var list: MutableList<CalenderEntry> = arrayListOf()

    var imagesLoaded: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemCalenderEntryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun getList():MutableList<CalenderEntry>{
        return this.list.toMutableList()
    }

    fun cleanList(){
        val listSize = this.list.size
        this.list = arrayListOf()
        imagesLoaded = 0
        notifyItemRangeRemoved(0,listSize)
    }

    fun setList(_list: MutableList<CalenderEntry>){
        cleanList()
        this.list = _list
        notifyItemRangeChanged(0,this.list.size)
    }

    fun appendList(_list: MutableList<CalenderEntry>){
        val listSize = this.list.size
        this.list = (this.list + _list).toMutableList()
        notifyItemRangeChanged(listSize,this.list.size)
    }


    fun updateItem(position: Int,item: CalenderEntry){
        list.removeAt(position)
        list.add(position,item)
        notifyItemChanged(position)
    }



    fun removeItem(position: Int){
        this.list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return this.list.size
    }


    inner class MyViewHolder(private val binding: ItemCalenderEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalenderEntry) {

            /**
             * Loading Images
             */


            // Load Recipe img
            loadRecipeImage(binding.recipeIV, item.recipe.imgSource) {
                imageLoadingListener.onImageLoaded()
            }


            /**
             * Details
             */


            binding.nameRecipeTV.text = item.recipe.title
            binding.tagTV.text = item.tag

            val tags = binding.root.context.resources.getStringArray(R.array.tagEntryCalender_items).toList()

            for (tag in tags){
                if(item.tag.lowercase() == tag.lowercase())
                    binding.tagTV.text = tag
            }
            binding.tagTV.backgroundTintList= getColorTag(item.tag)
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }


            binding.timeTV.text = formatServerTimeToTimeString(item.realizationDate)


            binding.checkDoneCB.isChecked = item.checkedDone

            binding.checkDoneCB.setOnClickListener {

                onDoneClicked.invoke(binding.checkDoneCB.isChecked,item)

            }

        }

        private fun getColorTag(tag: String): ColorStateList {

            var color: ColorStateList? = null

            when(tag){
                CALENDAR_MEALS_TAG.PEQUENO_ALMOCO -> color = binding.root.context.resources.getColorStateList(R.color.catg_peq_almoco, null)
                CALENDAR_MEALS_TAG.LANCHE_DA_MANHA -> color = binding.root.context.resources.getColorStateList(R.color.catg_lanche_manha, null)
                CALENDAR_MEALS_TAG.ALMOCO -> color = binding.root.context.resources.getColorStateList(R.color.catg_almoco, null)
                CALENDAR_MEALS_TAG.LANCHE_DA_TARDE -> color = binding.root.context.resources.getColorStateList(R.color.catg_lanche_tarde, null)
                CALENDAR_MEALS_TAG.JANTAR -> color = binding.root.context.resources.getColorStateList(R.color.catg_jantar, null)
                CALENDAR_MEALS_TAG.CEIA -> color = binding.root.context.resources.getColorStateList(R.color.catg_ceia, null)
            }

            return color!!
        }
    }




}
