package com.example.projectfoodmanager.presentation.calendar

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.databinding.ItemCalenderEntryBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToFormatTime
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class CalendarEntryAdapter(
    val onItemClicked: (Int, CalenderEntry) -> Unit,
    val onDoneClicked: (Boolean, CalenderEntry) -> Unit,
) : RecyclerView.Adapter<CalendarEntryAdapter.MyViewHolder>() {

    private val TAG: String = "CalenderEntryAdapter"
    private var list: MutableList<CalenderEntry> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemCalenderEntryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<CalenderEntry>){
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int,item: CalenderEntry){
        list.removeAt(position)
        list.add(position,item)
        notifyItemChanged(position)
    }


    fun cleanList(){
        this.list= arrayListOf()
        notifyDataSetChanged()
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
            if (item.recipe.img_source.isNotEmpty()){
                val imgRef = Firebase.storage.reference.child(item.recipe.img_source)
                imgRef.downloadUrl.addOnSuccessListener { Uri ->
                    val imageURL = Uri.toString()
                    Glide.with(binding.recipeIV.context).load(imageURL).into(binding.recipeIV)
                }.addOnFailureListener {
                    Glide.with(binding.recipeIV.context).load(R.drawable.img_default_recipe).into(binding.recipeIV)
                }
            }

            binding.nameRecipeTV.text = item.recipe.title
            binding.tagTV.text = item.tag

            val tags = binding.root.context.resources.getStringArray(R.array.tagEntryCalender_array).toList()

            for (tag in tags){
                if(item.tag.lowercase() == tag.lowercase())
                    binding.tagTV.text = tag
            }
            binding.tagTV.backgroundTintList= getColorTag(item.tag)
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(adapterPosition, item) }


            binding.timeTV.text = formatLocalTimeToFormatTime(LocalDateTime.parse(item.realization_date,DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")))


            //TODO: Melhorar por causa da sharedpreferences

            binding.checkDoneCB.isChecked = item.checked_done

            binding.checkDoneCB.setOnClickListener {

                onDoneClicked.invoke(binding.checkDoneCB.isChecked,item)

            }

        }

        private fun getColorTag(tag: String): ColorStateList {

            var color: ColorStateList? = null

            when(tag.uppercase()){
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
