package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.data.models.CreatedProductId
import com.project.morestore.databinding.FragmentCreateProductAddPhotoBinding
import com.project.morestore.domain.presenters.CreateProductPresenter
import com.project.morestore.presentation.dialogs.SaveProductDialog
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductAddPhotoFragment :
    MvpAppCompatFragment(R.layout.fragment_create_product_add_photo), CreateProductMvpView {
    private val binding: FragmentCreateProductAddPhotoBinding by viewBinding()
    private val args: CreateProductAddPhotoFragmentArgs by navArgs()
    @Inject lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }
    private var uriMap = mutableMapOf<Int, String>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
        initViews()
        getPhotosVideos()
    }


    private fun setClickListeners() {
        binding.addPhotoCardView.setOnClickListener {
           navigateToMakePhoto(1)
        }
        binding.addPhotoCardView2.setOnClickListener {
           navigateToMakePhoto(2)
        }
        binding.addPhotoCardView3.setOnClickListener {
           navigateToMakePhoto(3)
        }
        binding.addPhotoCardView4.setOnClickListener {
           navigateToMakeVideo(4)
        }

        binding.addPhotoCardView5.setOnClickListener {
           navigateToMakeVideo(5)
        }

        binding.addPhotoCardView6.setOnClickListener {
           navigateToMakeVideo(6)
        }

        binding.addPhotoCardView7.setOnClickListener {
           navigateToMakeVideo(7)
        }

        binding.addPhotoCardView8.setOnClickListener {
           navigateToMakeVideo(8)
        }

        binding.addPhotoCardView9.setOnClickListener {
           navigateToMakeVideo(9)
        }

        binding.addPhotoCardView10.setOnClickListener {
          navigateToMakeVideo(10)
        }

        binding.saveBtn.setOnClickListener {
            savePhotos()
        }


    }

    private fun initToolbar() {
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.setOnClickListener {
            if(args.product == null)
                SaveProductDialog { presenter.createDraftProduct() }.show(childFragmentManager, null)
            else
                findNavController().popBackStack(findNavController().previousBackStackEntry!!.destination.id, true)
        }

    }

    private fun initViews() {
        val htmlTaggedString = "<u>Требованиями к фотографиям.</u>";
        val textSpan = android.text.Html.fromHtml(htmlTaggedString)
        binding.photoRequirementsTextView.text = textSpan
        binding.example1ImageView.setImageResource(if (args.isShoos) R.drawable.shoos_example1 else R.drawable.image_cloth_example)
        binding.example2ImageView.setImageResource(if (args.isShoos) R.drawable.image_shoos_example2 else R.drawable.image_cloth_example2)
        binding.example3ImageView.setImageResource(if (args.isShoos) R.drawable.image_shoos_example3 else R.drawable.image_cloth_example3)
        if (args.isShoos.not()) {
            binding.bottomPhotoTextView.text = "Крупный план состава товара"
            binding.productMaterialInfoTextView.isVisible = true
        }


    }

    private fun getPhotosVideos() {
        presenter.loadCreateProductPhotosVideos()

    }

    private fun savePhotos(){
        if(uriMap.isNotEmpty())
            presenter.updateCreateProductDataPhotosVideosFromWeb(uriMap)
        else
            findNavController().popBackStack()
    }

    private fun navigateToMakePhoto(photoPos: Int){
        findNavController().navigate(CreateProductAddPhotoFragmentDirections
            .actionCreateProductAddPhotoFragmentToMakePhotoFragment(photoPos))
    }

    private fun navigateToMakeVideo(videoPos: Int){
        findNavController().navigate(CreateProductAddPhotoFragmentDirections
            .actionCreateProductAddPhotoFragmentToMakeVideoFragment(videoPos))
    }


    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        if(result is Unit){
            findNavController().popBackStack()
            return
        }

        if (result is CreatedProductId) {
            findNavController().navigate(R.id.mainFragment)
            return
        }

        val fileMap = result as MutableMap<Int, File>
        Log.d("mylog", fileMap[1]?.absolutePath.toString())
        val photoVideoUriMap = mutableMapOf<Int, String>()
        args.product?.photo?.forEachIndexed { index, productPhoto ->
            photoVideoUriMap[index + 1] = productPhoto.photo
        }

        args.product?.video?.forEachIndexed { index, productVideo ->
            photoVideoUriMap[index + 4] = productVideo.video
        }
        uriMap = photoVideoUriMap

        binding.addPhotoCardView6.isVisible = (fileMap[4] != null && fileMap[5] != null) || (uriMap[4] != null && uriMap[5] != null)
        binding.addPhotoCardView7.isVisible = fileMap[6] != null || uriMap[6] != null
        binding.addPhotoCardView8.isVisible = fileMap[7] != null || uriMap[7] != null
        binding.addPhotoCardView9.isVisible = fileMap[8] != null || uriMap[8] != null
        binding.addPhotoCardView10.isVisible = fileMap[9] != null || uriMap[9] != null


        Glide.with(this)
            .load(fileMap[1] ?: args.product?.photo?.first()?.photo)
            .into(binding.photo1ImageView)

        Glide.with(this)
            .load(
                fileMap[2] ?: uriMap[2]
            )
            .into(binding.photo2ImageView)

        Glide.with(this)
            .load(fileMap[3] ?: uriMap[3])
            .into(binding.photo3ImageView)

        Glide.with(this)
            .load(fileMap[4] ?: uriMap[4])
            .into(binding.photo4ImageView)

        Glide.with(this)
            .load(fileMap[5] ?: uriMap[5])
            .into(binding.photo5ImageView)

        Glide.with(this)
            .load(fileMap[6] ?: uriMap[6])
            .into(binding.photo6ImageView)

        Glide.with(this)
            .load(fileMap[7] ?: uriMap[7])
            .into(binding.photo7ImageView)

        Glide.with(this)
            .load(fileMap[8] ?: uriMap[8])
            .into(binding.photo8ImageView)

        Glide.with(this)
            .load(fileMap[9] ?: uriMap[9])
            .into(binding.photo9ImageView)

        Glide.with(this)
            .load(fileMap[10] ?: uriMap[10])
            .into(binding.photo10ImageView)


        if (fileMap.isNotEmpty()) {
            binding.saveBtn.backgroundTintList =
                ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
            binding.saveBtn.isEnabled = true
        } else {
            binding.saveBtn.backgroundTintList =
                ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray2, null))
            binding.saveBtn.isEnabled = false

        }

    }


    override fun loading() {
        binding.loader.isVisible = true

    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}