package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.databinding.FeedbackFragmentBinding

class FeedbackFragment :Fragment() {
    private lateinit var views :FeedbackFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FeedbackFragmentBinding.inflate(layoutInflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        views.feedback.addTextChangedListener {
            views.charCounter.text = "${it?.length ?: 0}/3000"
        }
        views.next.setOnClickListener {
            findNavController().navigate(R.id.action_feedbackFragment_to_feedbackPhotoFragment)
        }
    }
}