package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentMakePhotoBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MakePhotoFragment: Fragment(R.layout.fragment_make_photo) {
    private val binding: FragmentMakePhotoBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            binding.loader.progress = 0
            for(i in 1 until 31){
                binding.loader.incrementProgressBy(1)
                delay(1000)
            }
        }
    }
}