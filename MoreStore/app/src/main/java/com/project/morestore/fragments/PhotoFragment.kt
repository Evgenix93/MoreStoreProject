package com.project.morestore.fragments


import android.os.Bundle

import android.view.View

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide

import com.project.morestore.R
import com.project.morestore.databinding.FragmentPhotoBinding
import com.project.morestore.util.ClickWrapper

class PhotoFragment: Fragment(R.layout.fragment_photo) {
    private val binding: FragmentPhotoBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPhoto()

        binding.root.setOnClickListener {
            val clickWrapper = arguments?.getParcelable<ClickWrapper>(ON_CLICK)
            clickWrapper?.onClick?.invoke()
        }

        if(arguments?.getString(PHOTO)?.contains("mp4") == true)
            binding.playVideoImageView.isVisible = true

    }

    private fun loadPhoto(){
        Glide.with(this)
            .load(arguments?.getString(PHOTO))
            .into(binding.photoImageView)

        if(requireArguments().getBoolean(IS_SOLD, false))
            binding.photoImageView.alpha = 0.5f

    }

   companion object {
       const val IS_SOLD = "is sold"
       const val PHOTO = "photo"
       private const val ON_CLICK = "on_click"
       fun createInstance(photo: String, isSold: Boolean, onClick: () -> Unit): PhotoFragment {
           return PhotoFragment().apply {
               arguments = Bundle().apply {
                   putParcelable(ON_CLICK, ClickWrapper(onClick))
                   putString(PHOTO, photo)
                   putBoolean(IS_SOLD, isSold)
               }
           }
       }
   }
}