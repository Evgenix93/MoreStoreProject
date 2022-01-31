package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentPhotoBinding

class PhotoFragment: Fragment(R.layout.fragment_photo) {
    private val binding: FragmentPhotoBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if(arguments?.getString("photo") == "1"){
           // binding.photoImageView.setImageResource(R.drawable.item_product_photo)
       // }else{
           // binding.photoImageView.setImageResource(R.drawable.image_placeholder)
        //}
        loadPhoto()

    }

    private fun loadPhoto(){
        Glide.with(this)
            .load(arguments?.getString("photo"))
            .into(binding.photoImageView)
    }


}