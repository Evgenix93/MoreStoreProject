package com.project.morestore.fragments

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.updateBounds
import androidx.core.net.toUri
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.ortiz.touchview.OnTouchImageViewListener
import com.project.morestore.R
import com.project.morestore.databinding.FragmentPhotoFinishBinding
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.singletones.CreateProductData
import io.github.muddz.quickshot.QuickShot
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File

class PhotoFinishFragment: MvpAppCompatFragment(R.layout.fragment_photo_finish), MainMvpView {
    private val binding: FragmentPhotoFinishBinding by viewBinding()
    private val args: PhotoFinishFragmentArgs by navArgs()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var launcher: ActivityResultLauncher<Array<String>>? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showPhoto()
        initViews()
        setClickListeners()
        initLauncher()


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
                //requestPermission()
                savePhoto()
                //presenter.updateCreateProductDataPhotosVideos(File(args.photoFile), args.position)
            }
        }
    }


    private fun savePhoto(){

        val bitmap = binding.imageView27.drawToBitmap()
        val file = File(requireContext().cacheDir, "image.jpg")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
        CreateProductData.productPhotosPaths[1] = file
        findNavController().popBackStack(R.id.createProductAddPhotoFragment, false)
        //var file: File
        /*QuickShot.of(binding.imageView27).setResultListener(object : QuickShot.QuickShotListener{
            override fun onQuickShotSuccess(path: String?) {
                Log.d("mylog", "shot success")
               // file = File(path)
                Log.d("mylog", file.path)
                //Log.d("mylog", requireContext().filesDir.path)
                val file2 = File(requireContext().cacheDir, "photo.jpg")
                //file.mkdirs()
                //file2.mkdirs()
                //file.inputStream().use { input ->
                   // file2.outputStream().use { output ->
                    //input.copyTo(output)

                    //}

                //}

                //Glide.with(this@PhotoFinishFragment)
                    //.load(file2)
                    //.into(binding.backIcon)
                CreateProductData.productPhotosPaths[1] = file

                findNavController().popBackStack(R.id.createProductAddPhotoFragment, false)


            }

            override fun onQuickShotFailed(path: String?, errorMsg: String?) {

            }
        })
            //.enableLogging()
            //.setFilename(System.currentTimeMillis().toString())
            //.setPath(requireContext().cacheDir.path)
            //.save();
        //CreateProductData.productPhotosPaths[1] = file */




    }

    private fun requestPermission(){
        launcher?.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))

    }

    private fun initLauncher(){
        launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ map ->
            if(map.all { it.value }){
                savePhoto()
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