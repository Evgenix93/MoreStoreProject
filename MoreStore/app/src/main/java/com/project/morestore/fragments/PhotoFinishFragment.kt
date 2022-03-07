package com.project.morestore.fragments

import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.updateBounds
import androidx.core.net.UriCompat
import androidx.core.net.toUri
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.ResourceLoader
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ortiz.touchview.OnTouchImageViewListener
import com.project.morestore.R
import com.project.morestore.databinding.FragmentPhotoFinishBinding
import com.project.morestore.models.ProductPhoto
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.singletones.CreateProductData
import io.github.muddz.quickshot.QuickShot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.File
import java.io.InputStream

class PhotoFinishFragment: MvpAppCompatFragment(R.layout.fragment_photo_finish), MainMvpView {
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

        binding.imageView27.setOnTouchImageViewListener(object : OnTouchImageViewListener{
            override fun onMove() {
                binding.netImageView.isVisible = true
            }
        })

        binding.imageView27.background.setTint(ResourcesCompat.getColor(resources, R.color.black6, null))
        binding.root.background.setTint(ResourcesCompat.getColor(resources, R.color.black6, null))

    }

    private fun setClickListeners() {
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.secondOptionBtn.setOnClickListener {
            if (args.isVideo) {
                findNavController().popBackStack()
            }else {
                if(isBackgroundDeleted.not())
                deletePhotoBackground()
                else {
                    isBackgroundDeleted = false
                    binding.secondOptionBtn.text = "Удалить фон"
                    showPhoto()
                }
            }
        }

        binding.finishBtn.setOnClickListener {
            savePhoto()
        }
        binding.imageView27.setOnClickListener{
            Log.d("MyDebug", "photo onClick")
            playVideo()
        }
    }

    private fun savePhoto(){
        binding.imageView27.background.setTintList(ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.white, null)))
        val bitmap = binding.imageView27.drawToBitmap()
        presenter.updateCreateProductDataPhotosVideos(bitmap, args.position)
    }

    private fun deletePhotoBackground(){
        if(args.photoFile.contains("content")) {
            presenter.deletePhotoBackground(uri = args.photoFile.toUri())
        }else{
            presenter.deletePhotoBackground(file = File(args.photoFile))
        }
    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }

    private fun playVideo(){
       val intent = Intent().apply {
           action = Intent.ACTION_VIEW
           type = "video/mp4"
           val uri = FileProvider.getUriForFile(requireContext(), requireContext().applicationContext.packageName + ".provider", File(args.photoFile))
           data = uri
           flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
       }
        startActivity(intent)
    }

    override fun loaded(result: Any) {
        showLoading(false)
        binding.secondOptionBtn.text = "Вернуть фон"
        isBackgroundDeleted = true
        val photo = result as ProductPhoto
         Glide.with(this)
            .load(photo.photo)
             .into(binding.imageView27)

        binding.imageView27.background.setTint(ResourcesCompat.getColor(resources, R.color.white, null))

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

    override fun success() {
        showLoading(false)
        findNavController().popBackStack(R.id.createProductAddPhotoFragment, false)

    }
}