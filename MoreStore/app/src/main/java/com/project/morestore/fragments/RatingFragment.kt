package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.databinding.RatingFragmentBinding
import com.project.morestore.util.createRect
import com.project.morestore.util.dp

class RatingFragment : Fragment() {
    private lateinit var views :RatingFragmentBinding
    private val stars = arrayOfNulls<ImageView>(5)
    private var rating = 0
        set(value) {
            field = value
            views.next.isEnabled = value > 0
            for(filledIndex in 0 until value){
                stars[filledIndex]?.setImageResource(R.drawable.ic_star_filled)
            }
            for(emptyIndex in value until stars.size){
                stars[emptyIndex]?.setImageResource(R.drawable.ic_star_empty)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RatingFragmentBinding.inflate(layoutInflater, container, false)
        .also { views = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        configRating()
        views.rating.dividerDrawable = createRect(12.dp, 0)
        views.next.setOnClickListener {
            findNavController().navigate(R.id.action_ratingFragment_to_feedbackFragment)
        }
    }

    private fun configRating(){
        stars[0] = views.star1.apply { setOnClickListener { rating = 1 } }
        stars[1] = views.star2.apply { setOnClickListener { rating = 2 } }
        stars[2] = views.star3.apply { setOnClickListener { rating = 3 } }
        stars[3] = views.star4.apply { setOnClickListener { rating = 4 } }
        stars[4] = views.star5.apply { setOnClickListener { rating = 5 } }
    }
}