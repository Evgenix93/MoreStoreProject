package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductConditionBinding

class CreateProductConditionFragment: Fragment(R.layout.fragment_create_product_condition) {
    private val binding: FragmentCreateProductConditionBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initChecking()
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }

    private fun initChecking(){
        binding.newWithTagsClickView.setOnClickListener {
            setCheckActive(binding.newWithTagsCheckImageView)
        }
        binding.newWithoutTagsClickView.setOnClickListener {
            setCheckActive(binding.newWithoutTagsCheckImageView)
        }
        binding.exellentClickView.setOnClickListener {
            setCheckActive(binding.excellentCheckImageView)
        }
        binding.goodClickView.setOnClickListener {
            setCheckActive(binding.goodCheckImageView)
        }

    }

    private fun setCheckActive(image: ImageView){
        binding.newWithTagsCheckImageView.imageTintList = null  //drawable.setTint(ResourcesCompat.getColor(resources, R.color.gray1, null))
        binding.newWithoutTagsCheckImageView.imageTintList = null //drawable.setTint(ResourcesCompat.getColor(resources, R.color.gray1, null))
        binding.excellentCheckImageView.imageTintList = null //drawable.setTint(ResourcesCompat.getColor(resources, R.color.gray1, null))
        binding.goodCheckImageView.imageTintList = null //drawable.setTint(ResourcesCompat.getColor(resources, R.color.gray1, null))

        image.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.green, null)) //drawable.setTint(ResourcesCompat.getColor(resources, R.color.green, null))





    }


}