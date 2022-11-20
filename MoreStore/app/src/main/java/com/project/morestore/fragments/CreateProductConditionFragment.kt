package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductConditionBinding
import com.project.morestore.dialogs.SaveProductDialog
import com.project.morestore.data.models.CreateProductData
import com.project.morestore.data.models.CreatedProductId
import com.project.morestore.data.models.Property2
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductConditionFragment: MvpAppCompatFragment(R.layout.fragment_create_product_condition), MainMvpView {
    private val binding: FragmentCreateProductConditionBinding by viewBinding()
    @Inject lateinit var mainPresenter: MainPresenter
    private val presenter by moxyPresenter { mainPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initChecking()
        initSaveButton()
        loadCondition()
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
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

    private fun initSaveButton(){
        binding.saveButton.setOnClickListener{
            saveCondition()
            findNavController().popBackStack()
        }
    }

    private fun setCheckActive(image: ImageView){
        binding.newWithTagsCheckImageView.imageTintList = null  //drawable.setTint(ResourcesCompat.getColor(resources, R.color.gray1, null))
        binding.newWithoutTagsCheckImageView.imageTintList = null //drawable.setTint(ResourcesCompat.getColor(resources, R.color.gray1, null))
        binding.excellentCheckImageView.imageTintList = null //drawable.setTint(ResourcesCompat.getColor(resources, R.color.gray1, null))
        binding.goodCheckImageView.imageTintList = null //drawable.setTint(ResourcesCompat.getColor(resources, R.color.gray1, null))

        image.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.green, null)) //drawable.setTint(ResourcesCompat.getColor(resources, R.color.green, null))
        binding.saveButton.isEnabled = true
        binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
    }

   private fun saveCondition(){
     val property =  when {
           binding.newWithTagsCheckImageView.imageTintList != null -> Property2(111, 11)
           binding.newWithoutTagsCheckImageView.imageTintList != null -> Property2(112, 11)
           binding.excellentCheckImageView.imageTintList != null -> Property2(113, 11)
           binding.goodCheckImageView.imageTintList != null -> Property2(114, 11)
           else -> Property2(111, 11)
       }
       presenter.removeProperty(11)
       presenter.updateCreateProductData(extProperty = property)
   }

    private fun loadCondition(){
        presenter.loadCreateProductData()
    }

    override fun loaded(result: Any) {

        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }


       val createProductData = result as CreateProductData
        binding.toolbar.actionIcon.setOnClickListener {
            if(result.id == null)
                SaveProductDialog { presenter.createDraftProduct() }.show(childFragmentManager, null)
            else
                findNavController().popBackStack(findNavController().previousBackStackEntry!!.destination.id, true)
        }
       val conditionProperty = createProductData.property?.find{it.propertyCategory == 11L} ?: return
       when (conditionProperty.value){
           111L -> {setCheckActive(binding.newWithTagsCheckImageView)}
           112L -> setCheckActive(binding.newWithoutTagsCheckImageView)
           113L -> setCheckActive(binding.excellentCheckImageView)
           114L -> setCheckActive(binding.goodCheckImageView)
       }
    }

    override fun loading() {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun success() {

    }
}