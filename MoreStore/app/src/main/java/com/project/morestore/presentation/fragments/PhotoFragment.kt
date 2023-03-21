package com.project.morestore.presentation.fragments


import android.os.Bundle

import android.view.View

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide

import com.project.morestore.R
import com.project.morestore.databinding.FragmentPhotoBinding


class PhotoFragment: Fragment(R.layout.fragment_photo) {
    private val binding: FragmentPhotoBinding by viewBinding()
    var onClick: () -> Unit = {}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPhoto()

        binding.root.setOnClickListener {
            onClick()
        }

        if(arguments?.getString(PHOTO)?.contains("mp4") == true)
            binding.playVideoImageView.isVisible = true

    }

    private fun loadPhoto(){
        Glide.with(this)
            .load(arguments?.getString(PHOTO))
            .into(binding.photoImageView)

        if(arguments?.getBoolean(IS_SOLD, false) == true)
            binding.photoImageView.alpha = 0.5f

    }

   companion object {
       const val IS_SOLD = "is sold"
       const val PHOTO = "photo"
       fun createInstance(photo: String, isSold: Boolean, onClick: () -> Unit): PhotoFragment {
           return PhotoFragment().apply {
               this.onClick = onClick
               arguments = Bundle().apply {
                   putString(PHOTO, photo)
                   putBoolean(IS_SOLD, isSold)
               }
           }
       }
   }
}