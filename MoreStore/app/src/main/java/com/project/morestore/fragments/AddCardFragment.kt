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
import com.project.morestore.mvpviews.AddCardMvpView
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.AddCardPresenter
import com.project.morestore.presenters.MainPresenter
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class AddCardFragment: MvpAppCompatFragment(R.layout.fragment_add_card), AddCardMvpView {
    private val binding: FragmentAddCardBinding by viewBinding()
    @Inject private lateinit var addCardPresenter: AddCardPresenter
    private val presenter by moxyPresenter { addCardPresenter }

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

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    }

    override fun success() {
        findNavController().popBackStack()
    }
}