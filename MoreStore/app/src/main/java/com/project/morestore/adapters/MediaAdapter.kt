package com.project.morestore.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentPhotoBinding

class MediaAdapter(host :Fragment, val photos :Array<String>, val onClick: (String) -> Unit) :FragmentStateAdapter(host) {
    override fun getItemCount(): Int = photos.size

    override fun createFragment(position: Int) = MediaFragment(photos[position]) {
        onClick(photos[position])
    }

    class MediaFragment(val onClick: () -> Unit, val photo: String) :Fragment(){
        private lateinit var binding: FragmentPhotoBinding

        companion object{
            const val PHOTO = "photo"
        }

        constructor(photo :String, onClick: () -> Unit) :this(onClick, photo){
            arguments = bundleOf(PHOTO to photo)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ) = FragmentPhotoBinding.inflate(inflater).also { binding = it }.root /*ImageView(requireContext())
            .apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                Glide.with(requireContext())
                    .load(requireArguments().getString(PHOTO))
                    .into(this)

                //foreground = this@MediaFragment.requireContext().getDrawable(R.drawable.ic_play_video)
            }*/

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            view.setOnClickListener {
                onClick()
            }

            Glide.with(this)
                .load(photo)
                .into(binding.photoImageView)

            if(photo.contains("mp4") || requireContext().contentResolver.getType(photo.toUri())?.contains("mp4") == true)
                binding.playVideoImageView.isVisible = true
        }
    }
}