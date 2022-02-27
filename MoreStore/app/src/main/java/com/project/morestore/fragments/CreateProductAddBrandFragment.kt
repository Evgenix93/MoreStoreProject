package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentBrandNotFoundBinding
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductAddBrandFragment: MvpAppCompatFragment(R.layout.fragment_brand_not_found), MainMvpView {
    private val binding: FragmentBrandNotFoundBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        setClickListeners()
        initEditText()
    }


    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.isVisible = false
    }

    private fun setClickListeners(){
        binding.sendBtn.setOnClickListener {
            presenter.addNewBrand(binding.brandNameEditText.text.toString())
        }
    }

    private fun initEditText(){
        binding.brandNameEditText.addTextChangedListener { text ->
            if(text.isNullOrBlank()){
                binding.sendBtn.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray2, null))
                binding.sendBtn.isEnabled = false
            }else{
                binding.sendBtn.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
                binding.sendBtn.isEnabled = true
            }

        }
    }

    override fun loaded(result: Any) {


    }

    override fun loading() {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun success() {

    }
}