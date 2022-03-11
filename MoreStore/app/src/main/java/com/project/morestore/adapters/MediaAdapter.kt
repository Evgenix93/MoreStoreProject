package com.project.morestore.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.project.morestore.R

class MediaAdapter(host :Fragment, val photos :Array<String>) :FragmentStateAdapter(host) {
    override fun getItemCount(): Int = photos.size

    override fun createFragment(position: Int) = MediaFragment(photos[position])

    class MediaFragment :Fragment{

        companion object{
            const val PHOTO = "photo"
        }

        constructor(photo :String) :super(){
            arguments = bundleOf(PHOTO to photo)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ) = ImageView(requireContext())
            .apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                Glide.with(requireContext())
                    .load(requireArguments().getString(PHOTO))
                    .into(this)
            }
    }
}