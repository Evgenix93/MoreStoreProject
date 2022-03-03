package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.adapters.FeedbackPhotosAdapter
import com.project.morestore.databinding.FragmentFeedbackPhotosBinding
import com.project.morestore.dialogs.FeedbackCompleteDialog
import com.project.morestore.models.FeedbackItem

class FeedbackPhotoFragment :Fragment(){
    private lateinit var views :FragmentFeedbackPhotosBinding
    private var current = 0
    private val adapter = FeedbackPhotosAdapter{
        if(it is FeedbackItem.AddPhoto){
            when(current){
                0 -> { current = 1; photo1.run(); views.send.setText(R.string.send) }
                1 -> { current = 2; photo2.run() }
                2 -> { current = 3; photo3.run() }
                3 -> { current = 4; photo4.run() }
                4 -> { current = 5; photo5.run() }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFeedbackPhotosBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        views.photos.adapter = adapter
        adapter.setItems(empty)
        views.send.setOnClickListener {
            FeedbackCompleteDialog(requireContext()){
                findNavController().popBackStack(R.id.cabinetFragment, false)
            }.show()
        }
    }

    private val empty = listOf(FeedbackItem.AddPhoto, FeedbackItem.Description)

    private val photo1 :Runnable get() = Runnable {
        adapter.setItems(
            listOf(FeedbackItem.Photo(R.drawable.feedback_photo_1), FeedbackItem.AddPhoto)
        )
    }

    private val photo2 :Runnable get() = Runnable {
        adapter.setItems(
            listOf(
                FeedbackItem.Photo(R.drawable.feedback_photo_1),
                FeedbackItem.Photo(R.drawable.feedback_photo_2),
                FeedbackItem.AddPhoto
            )
        )
    }

    private val photo3 :Runnable get() = Runnable {
        adapter.setItems(
            listOf(
                FeedbackItem.Photo(R.drawable.feedback_photo_1),
                FeedbackItem.Photo(R.drawable.feedback_photo_2),
                FeedbackItem.Photo(R.drawable.feedback_photo_3),
                FeedbackItem.AddPhoto
            )
        )
    }

    private val photo4 :Runnable get() = Runnable{
        adapter.setItems(
            listOf(
                FeedbackItem.Photo(R.drawable.feedback_photo_1),
                FeedbackItem.Photo(R.drawable.feedback_photo_2),
                FeedbackItem.Photo(R.drawable.feedback_photo_3),
                FeedbackItem.Photo(R.drawable.feedback_photo_4),
                FeedbackItem.AddPhoto
            )
        )
    }

    private val photo5 :Runnable get() = Runnable{
        adapter.setItems(
            listOf(
                FeedbackItem.Photo(R.drawable.feedback_photo_1),
                FeedbackItem.Photo(R.drawable.feedback_photo_2),
                FeedbackItem.Photo(R.drawable.feedback_photo_3),
                FeedbackItem.Photo(R.drawable.feedback_photo_4),
                FeedbackItem.Photo(R.drawable.feedback_photo_1),
            )
        )
    }
}