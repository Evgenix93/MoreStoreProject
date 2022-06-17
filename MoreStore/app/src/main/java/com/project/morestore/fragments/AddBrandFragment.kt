package com.project.morestore.fragments

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
import com.project.morestore.mvpviews.NewBrandView
import com.project.morestore.presenters.NewBrandPresenter
import com.project.morestore.singletones.Network
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class AddBrandFragment : MvpAppCompatFragment(R.layout.fragment_brand_not_found), NewBrandView {
    companion object {
        const val REQUEST_BRAND = "request_brand"
        const val BRAND = "brand"
        const val BRAND_ID= "brand_id"
    }
    private val binding: FragmentBrandNotFoundBinding by viewBinding()
    private val presenter by moxyPresenter { NewBrandPresenter(Network.brandApi) }

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

    override fun finish(brand :String, brandId: Long) {
        setFragmentResult(REQUEST_BRAND, bundleOf(BRAND to brand, BRAND_ID to brandId ))
        findNavController().navigateUp()
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    //endregion View implementation
}