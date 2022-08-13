package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentAddCardBinding
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class AddCardFragment: MvpAppCompatFragment(R.layout.fragment_add_card), MainMvpView {
    private val binding: FragmentAddCardBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditTexts()
        initToolbar()
        setClickListeners()
    }


    private fun initEditTexts(){
        val cardNumberListener = MaskedTextChangedListener("[0000]-[0000]-[0000]-[0000]", binding.editText)
        binding.editText.addTextChangedListener(cardNumberListener)
        val cardDateListener = MaskedTextChangedListener("[00]/[00]", binding.editText4)
        binding.editText4.addTextChangedListener(cardDateListener)
        val cardCvvListener = MaskedTextChangedListener("[000]", binding.editText2)
        binding.editText2.addTextChangedListener(cardCvvListener)
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.isVisible = false
    }

    private fun setClickListeners(){
        binding.payButton.setOnClickListener {
            presenter.addCard(binding.editText.text.toString())
        }
    }

    override fun loaded(result: Any) {

    }

    override fun loading() {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun loginFailed() {

    }

    override fun success() {
        findNavController().popBackStack()

    }
}