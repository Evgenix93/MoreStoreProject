package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentPhotoFinishBinding
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File

class PhotoFinishFragment: MvpAppCompatFragment(R.layout.fragment_photo_finish), MainMvpView {
    private val binding: FragmentPhotoFinishBinding by viewBinding()
    private val args: PhotoFinishFragmentArgs by navArgs()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showPhoto()
        initViews()
        setClickListeners()


    }

    private fun showPhoto(){
        if(args.photoFile.contains("content")){
            Glide.with(this)
                .load(args.photoFile.toUri())
                .into(binding.imageView27)
        }else {
            val file = File(args.photoFile)
            Glide.with(this)
                .load(file)
                .into(binding.imageView27)
        }
    }

    private fun initViews(){
        if(args.isVideo){
            binding.playImageView.isVisible = true
            binding.secondOptionBtn.text = "Переснять"
        }
    }

    private fun setClickListeners() {
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.secondOptionBtn.setOnClickListener {
            if (args.isVideo) {
                findNavController().popBackStack()
            }
        }

        binding.finishBtn.setOnClickListener {
            if (args.photoFile.contains("content")) {
                presenter.updateCreateProductDataPhotosVideos(args.photoFile.toUri(), args.position)
            } else {
                presenter.updateCreateProductDataPhotosVideos(File(args.photoFile), args.position)
            }
        }
    }








    override fun loaded(result: Any) {

    }

    override fun loading() {

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun success() {
        findNavController().popBackStack(R.id.createProductAddPhotoFragment, false)

    }
}