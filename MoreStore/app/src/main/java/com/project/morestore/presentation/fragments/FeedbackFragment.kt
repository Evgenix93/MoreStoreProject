package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.databinding.FeedbackFragmentBinding

class FeedbackFragment :Fragment() {
    companion object{
        const val FEEDBACK = "feedback"
    }
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
            val length = it?.length ?: 0
            views.next.isEnabled = length > 0
            views.charCounter.text = "$length/3000"
        }
        views.next.setOnClickListener {
            findNavController()
                .navigate(R.id.feedbackPhotoFragment,
                    bundleOf(FEEDBACK to views.feedback.text.toString())
                        .apply { putAll(requireArguments()) }
                )
        }
    }
}