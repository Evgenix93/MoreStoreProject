package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentBrandNotFoundBinding
import com.project.morestore.presentation.mvpviews.NewBrandView
import com.project.morestore.domain.presenters.NewBrandPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class AddBrandFragment : MvpAppCompatFragment(R.layout.fragment_brand_not_found), NewBrandView {
    companion object {
        const val REQUEST_BRAND = "request_brand"
        const val BRAND = "brand"
        const val BRAND_ID= "brand_id"
    }
    private val binding: FragmentBrandNotFoundBinding by viewBinding()
    @Inject lateinit var newBrandPresenter: NewBrandPresenter
    private val presenter by moxyPresenter { newBrandPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.toolbar) {
            backIcon.setOnClickListener { findNavController().popBackStack() }
            actionIcon.isVisible = false
        }
        with(binding.sendBtn){
            setOnClickListener {
                presenter.createBrand(binding.brandNameEditText.text.toString())
            }
        }
        with(binding.brandNameEditText){
            addTextChangedListener { text ->
                if (text.isNullOrBlank()) {
                    binding.sendBtn.backgroundTintList =
                        ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray2, null))
                    binding.sendBtn.isEnabled = false
                } else {
                    binding.sendBtn.backgroundTintList =
                        ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
                    binding.sendBtn.isEnabled = true
                }
            }
        }
    }

    //region View implementation
    override fun loading(show: Boolean) {
        binding.loader.isVisible = show
    }

    override fun finish(brandId: Long, brandName: String) {
        setFragmentResult(REQUEST_BRAND, bundleOf( BRAND_ID to brandId, BRAND to brandName ))
        findNavController().navigateUp()
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    //endregion View implementation
}