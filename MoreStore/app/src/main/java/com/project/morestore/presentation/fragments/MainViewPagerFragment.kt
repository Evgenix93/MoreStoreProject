package com.project.morestore.presentation.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemBannerBinding
import com.project.morestore.data.models.Banner

class MainViewPagerFragment: Fragment(R.layout.item_banner) {
    private val binding: ItemBannerBinding by viewBinding()
    private lateinit var banner: Banner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    private fun bind(){
        Glide.with(this)
            .load(banner.photo?.photo)
            .into(binding.bannerImage)

        binding.titleTextView.text = banner.title
        binding.descriptionTextView.text = Html.fromHtml(banner.text)
        binding.titleTextView.setTextColor(Color.parseColor(banner.color.title))
        binding.descriptionTextView.setTextColor(Color.parseColor(banner.color.text))

    }

    companion object {
        fun createInstance(banner: Banner): MainViewPagerFragment{
            return MainViewPagerFragment().apply { this.banner = banner }
        }
    }
}