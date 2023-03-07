package com.example.projectfoodmanager.presentation.recipe.comments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.data.model.Comment
import com.example.projectfoodmanager.databinding.FragmentCommentsBinding

class CommentsFragment : Fragment() {

    lateinit var binding: FragmentCommentsBinding
    private lateinit var viewModel: CommentsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (this::binding.isInitialized){
            return binding.root
        }else {
            // Inflate the layout for this fragment
            binding = FragmentCommentsBinding.inflate(layoutInflater)

            return binding.root
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)

        updateUI()
    }

    private fun updateUI() {
        // TODO: Passar a array dos comentarios

        binding.LVComments.isClickable = false


        val itemsAdapterComments: CommentsListingAdapter? =
            this.context?.let { CommentsListingAdapter(it,generateComments()) }
        binding.LVComments.adapter = itemsAdapterComments
        //setListViewHeightBasedOnChildren(binding.LVIngridientsInfo)




    }

    private fun generateComments(): MutableList<Comment> {

        val list_comments : MutableList<Comment> = arrayListOf()

        list_comments.add(Comment("Antonio Afonso","23-04-2001","Adorei esta merda de receita",4))
        list_comments.add(Comment("Maria Anacleta","02-08-2009","Tenha alguns modos se fizer favor",10))
        list_comments.add(Comment("João Manuel","12-03-1989","Você ponha se no crlh",30))
        list_comments.add(Comment("Ana Esgroveia","21-04-1922","Adorei a receita, tenho pena que exista pessoas que nao sabem se comportar",4))
        list_comments.add(Comment("Carlos Vinagre","10-04-1988","Devia de haver moderadores que vissem estes comentarios",4))
        list_comments.add(Comment("Antonio Barbosa","03-11-2004","Tu põente no crlh antes que ele se ponha em ti",50))
        list_comments.add(Comment("Rafael Silva","11-04-2000","Em vez de se insultarem experimentem a receita cookies de weed, acho que nao se vão arrepender",15))
        list_comments.add(Comment("Clara Carvalho","22-04-2011","Ja experimentei e adorei é pena que começei a ver varios animais",4))
        list_comments.add(Comment("Carlos Vinagre","10-04-1988","Curteee, estamos a falar da receita ou de passas fdd?, se for para isso vou dormir",4))
        list_comments.add(Comment("Antonio Barbosa","03-11-2004","Pensava que já estavas a dormir",50))

        return list_comments
    }


}