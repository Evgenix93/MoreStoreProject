package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentPhotoBinding

class PhotoFragment(val onClick: () -> Unit): Fragment(R.layout.fragment_photo) {
    private val binding: FragmentPhotoBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPhoto()

        binding.root.setOnClickListener {
            onClick()
        }

        if(arguments?.getString("photo")?.contains("mp4") == true)
            binding.playVideoImageView.isVisible = true

    }

    private fun loadPhoto(){
        Glide.with(this)
            .load(arguments?.getString("photo"))
            .into(binding.photoImageView)
    }


}