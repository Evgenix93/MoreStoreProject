package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.adapters.FeedbackProductsAdapter
import com.project.morestore.databinding.FragmentFeedbackProductsBinding
import com.project.morestore.fragments.base.FullscreenFragment
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace

class FeedbackProductFragment :FullscreenFragment() {
    private lateinit var views :FragmentFeedbackProductsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFeedbackProductsBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        views.list.adapter = FeedbackProductsAdapter{
            findNavController().navigate(R.id.action_feedbackProductFragment_to_ratingFragment)
        }
        views.list.setSpace(8.dp)
    }
}