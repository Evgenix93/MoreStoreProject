package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.project.morestore.databinding.FragmentPhotoBinding
import com.project.morestore.util.ClickWrapper

class MediaFragmentSmall : Fragment(){
    private lateinit var binding: FragmentPhotoBinding

    companion object{
        private const val PHOTO = "photo"
        private const val ON_CLICK = "on_click"
        fun create(photo: String, onClick: () -> Unit): MediaFragmentSmall{
            return MediaFragmentSmall().apply { arguments =
            bundleOf(PHOTO to photo, ON_CLICK to ClickWrapper(onClick))
            }

        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPhotoBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {
            val clickWrapper = arguments?.getParcelable<ClickWrapper>(ON_CLICK)
            clickWrapper?.onClick?.invoke()
        }

        val photo = arguments?.getString(PHOTO)

        Glide.with(this)
            .load(photo)
            .into(binding.photoImageView)

        if(photo.orEmpty().contains("mp4") || requireContext().contentResolver.getType(photo.orEmpty().toUri())?.contains("mp4") == true)
            binding.playVideoImageView.isVisible = true
    }
}