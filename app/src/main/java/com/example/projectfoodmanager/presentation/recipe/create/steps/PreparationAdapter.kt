package com.example.projectfoodmanager.presentation.recipe.create.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Preparation
import com.example.projectfoodmanager.databinding.ItemCreateRecipePreparationLayoutBinding


class PreparationAdapter(
    items: MutableList<Preparation>,
    val onItemClicked: (Int,Preparation) -> Unit,
    val onUpdateClicked: (Int,Preparation) -> Unit,
) : RecyclerView.Adapter<PreparationAdapter.MyViewHolder>() {

    private val TAG: String? = "IngredientsAdapter"
    private var list: MutableList<Preparation> = arrayListOf()

    private var currentOptionON: CardView? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemCreateRecipePreparationLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }


    fun getList(): MutableList<Preparation> {
        return list
    }

    fun updateList(list: MutableList<Preparation>){
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int,item: Preparation){
        list.removeAt(position)
        //var ingrid = list.get(position)

        list.add(position,item)
        notifyItemChanged(position)
    }


    fun addItem(item: Preparation){
        // Add the new item to the data set
        list.add(item)

        // Notify the adapter that the data set has changed
        // Notify the adapter that an item was inserted

        notifyDataSetChanged()

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


    inner class MyViewHolder(private val binding: ItemCreateRecipePreparationLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Preparation) {

            binding.stepTV.text= (bindingAdapterPosition + 1).toString()
            //binding.numberStepTv.text= item.step.toString()
            binding.discriptionTV.text= item.description

            binding.itemLayout.setOnClickListener {
                onItemClicked.invoke(bindingAdapterPosition,item)

                //Hide optionCV of last selected item
                if(currentOptionON != null && currentOptionON != binding.optionsCV) {
                    currentOptionON?.visibility=View.GONE
                }

                //Save optionCV for currently selected item
                currentOptionON=binding.optionsCV

                //Change visibility for currently selected item
                if (binding.optionsCV.visibility == View.VISIBLE){
                    binding.optionsCV.visibility= View.GONE
                }else{
                    binding.optionsCV.visibility = View.VISIBLE
                }

            }

            binding.updateIB.setOnClickListener {
                //updateItem(bindingAdapterPosition,item)
                onUpdateClicked.invoke(bindingAdapterPosition,item)
            }

            binding.removeIB.setOnClickListener {
                removeItem(bindingAdapterPosition)
            }

        }
    }




}
