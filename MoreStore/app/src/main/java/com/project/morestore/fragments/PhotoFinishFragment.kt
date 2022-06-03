package com.project.morestore.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.ortiz.touchview.OnTouchImageViewListener
import com.project.morestore.R
import com.project.morestore.databinding.FragmentPhotoFinishBinding
import com.project.morestore.models.ProductPhoto
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File

class PhotoFinishFragment : MvpAppCompatFragment(R.layout.fragment_photo_finish), MainMvpView {
    private val binding: FragmentPhotoFinishBinding by viewBinding()
    private val args: PhotoFinishFragmentArgs by navArgs()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var isBackgroundDeleted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showPhoto()
        initViews()
        setClickListeners()
    }

    private fun showPhoto() {
        if (args.photoFile.contains("content")) {
            Glide.with(this)
                .load(args.photoFile.toUri())
                .into(binding.imageView27)
        } else {
            val file = File(args.photoFile)
            Glide.with(this)
                .load(file)
                .into(binding.imageView27)
        }

    }

    private fun initViews() {
        if (args.isVideo) {
            binding.playImageView.isVisible = true
            binding.secondOptionBtn.text = "Переснять"
        }

        binding.imageView27.setOnTouchImageViewListener(object : OnTouchImageViewListener {
            override fun onMove() {
                if (args.photoFile.contains("mp4")
                        .not() && requireContext().contentResolver.getType(args.photoFile.toUri())
                        ?.contains("mp4") != true
                )
                    binding.netImageView.isVisible = true
            }
        })

        binding.imageView27.background.setTint(
            ResourcesCompat.getColor(
                resources,
                R.color.black6,
                null
            )
        )
        binding.root.background.setTint(ResourcesCompat.getColor(resources, R.color.black6, null))

    }

    private fun setClickListeners() {
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.secondOptionBtn.setOnClickListener {
            if (args.isVideo) {
                findNavController().popBackStack()
            } else {
                if (isBackgroundDeleted.not())
                    deletePhotoBackground()
                else {
                    isBackgroundDeleted = false
                    binding.secondOptionBtn.text = "Удалить фон"
                    showPhoto()
                }
            }
        }

        binding.finishBtn.setOnClickListener {
            if (args.isVideo)
                if (args.photoFile.contains("content"))
                    presenter.updateCreateProductDataPhotosVideos(
                        args.photoFile.toUri(),
                        args.position
                    )
                else presenter.updateCreateProductDataPhotosVideos(
                    File(args.photoFile),
                    args.position
                )
            else savePhoto()


        }
        binding.imageView27.setOnClickListener {
            Log.d("MyDebug", "photo onClick")
            if (args.isVideo)
                playVideo()
        }
    }

    private fun savePhoto() {
        binding.imageView27.background.setTintList(
            ColorStateList.valueOf(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white,
                    null
                )
            )
        )
        val bitmap = binding.imageView27.drawToBitmap()
        presenter.updateCreateProductDataPhotosVideos(bitmap, args.position)
    }

    private fun deletePhotoBackground() {
        if (args.photoFile.contains("content")) {
            presenter.deletePhotoBackground(uri = args.photoFile.toUri())
        } else {
            presenter.deletePhotoBackground(file = File(args.photoFile))
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.loader.isVisible = loading
    }

    private fun playVideo() {
        if (args.photoFile.contains("content"))
            presenter.playVideo(fileUri = args.photoFile.toUri())
        else
            presenter.playVideo(file = File(args.photoFile))
    }

    override fun loaded(result: Any) {
        showLoading(false)
        if (result is Intent) {
            startActivity(result)
            return
        }

        binding.secondOptionBtn.text = "Вернуть фон"
        isBackgroundDeleted = true
        val photo = result as ProductPhoto
        Glide.with(this)
            .load(photo.photo)
            .into(binding.imageView27)

        binding.imageView27.background.setTint(
            ResourcesCompat.getColor(
                resources,
                R.color.white,
                null
            )
        )

    }

    override fun loading() {
        showLoading(true)

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun loginFailed() {
        TODO("Not yet implemented")
    }

    override fun success() {
        showLoading(false)
        val isFromCreateProduct = findNavController().backQueue[findNavController().backQueue.size - 3].destination.id == R.id.createProductAddPhotoFragment
        findNavController().popBackStack(
            if(isFromCreateProduct) R.id.createProductAddPhotoFragment else R.id.orderProblemsPhotoFragment, false)

    }
}