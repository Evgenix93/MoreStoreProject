package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.databinding.RatingFragmentBinding

class RatingFragment : Fragment() {
    companion object{
        const val RATE = "rate"
    }
    private lateinit var views :RatingFragmentBinding

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
        views.next.setOnClickListener {
            findNavController()
                .navigate(R.id.feedbackFragment,
                    bundleOf(RATE to views.rating.rate.toByte())
                        .apply { putAll(requireArguments()) }
                )
        }
        views.rating.callback = {
            views.next.isEnabled = it > 0
        }
    }
}