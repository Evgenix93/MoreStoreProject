package com.project.morestore.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemBannerBinding
import com.project.morestore.models.Banner

class MainViewPagerFragment(private val banner: Banner): Fragment(R.layout.item_banner) {
    private val binding: ItemBannerBinding by viewBinding()

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
}