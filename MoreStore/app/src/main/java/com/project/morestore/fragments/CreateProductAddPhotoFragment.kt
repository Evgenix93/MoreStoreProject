package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductAddPhotoBinding

class CreateProductAddPhotoFragment: Fragment(R.layout.fragment_create_product_add_photo) {
    private val binding: FragmentCreateProductAddPhotoBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
    }



    private fun setClickListeners(){
        binding.addPhotoCardView.setOnClickListener {
            findNavController().navigate(CreateProductAddPhotoFragmentDirections.actionCreateProductAddPhotoFragmentToMakePhotoFragment())
        }
        binding.addPhotoCardView2.setOnClickListener {
            findNavController().navigate(CreateProductAddPhotoFragmentDirections.actionCreateProductAddPhotoFragmentToMakePhotoFragment())
        }
        binding.addPhotoCardView3.setOnClickListener {
            findNavController().navigate(CreateProductAddPhotoFragmentDirections.actionCreateProductAddPhotoFragmentToMakePhotoFragment())
        }
        binding.addPhotoCardView4.setOnClickListener {
            findNavController().navigate(CreateProductAddPhotoFragmentDirections.actionCreateProductAddPhotoFragmentToMakeVideoFragment())
        }

        binding.addPhotoCardView5.setOnClickListener {
            findNavController().navigate(CreateProductAddPhotoFragmentDirections.actionCreateProductAddPhotoFragmentToMakeVideoFragment())
        }


    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }
}