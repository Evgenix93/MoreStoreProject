package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductNameBinding
import com.project.morestore.domain.presenters.CreateProductPresenter
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductNameFragment : MvpAppCompatFragment(R.layout.fragment_create_product_name), CreateProductMvpView {
    private val binding: FragmentCreateProductNameBinding by viewBinding()
    @Inject lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }
    private val args: CreateProductNameFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditText()
        initToolbar()
        setClickListeners()
    }


    private fun initEditText() {
        binding.nameEditText.addTextChangedListener { text ->
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

    private fun setClickListeners() {
        binding.sendBtn.setOnClickListener {
            presenter.updateCreateProductData(name = binding.nameEditText.text.toString())
            findNavController().navigate(
                CreateProductNameFragmentDirections.actionCreateProductNameFragmentToCreateProductStep6Fragment(
                    category = args.category,
                    brand = args.brand,
                    forWho = args.forWho
                )
            )
        }
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "шаг 4 из 5"
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false

    }

    override fun loading() {
        binding.loader.isVisible = true

    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


}