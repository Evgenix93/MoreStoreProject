package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration1Binding
import androidx.core.view.isVisible
import com.project.morestore.MainActivity
import com.project.morestore.domain.presenters.RegistrationPresenter
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.presentation.mvpviews.RegistrationMvpView
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class Registration4Fragment : MvpAppCompatFragment(R.layout.fragment_registration1), RegistrationMvpView {
    private val binding: FragmentRegistration1Binding by viewBinding()
    private val args: Registration4FragmentArgs by navArgs()
    @Inject
    lateinit var registrationPresenter: RegistrationPresenter
    private val presenter by moxyPresenter { registrationPresenter }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSoftInputMode()
        setClickListeners()
        initToolbar()
        initEditText()
    }

    private fun setSoftInputMode(){
        (activity as MainActivity).changeSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun setClickListeners() {
        val isEmail = args.phoneOrEmail.contains(Regex("[a-z]")) || args.phoneOrEmail.isEmpty()
        if (!isEmail) {
            binding.skipStepTextView.isVisible = true
            binding.skipStepTextView.setOnClickListener {
                findNavController().navigate(
                    Registration4FragmentDirections.actionRegistration4FragmentToOnboarding1Fragment()
                )
            }
        }
        binding.getCodeBtn.setOnClickListener {
            if (!isEmail) {
                presenter.changeUserData(email = binding.phoneEmailEditText.text.toString())
            } else {
                presenter.changeUserData(phone = binding.phoneEmailEditText.text.toString())
            }
        }
    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Последний шаг"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initEditText() {
        if (!args.phoneOrEmail.contains(Regex("[a-z]")) && args.phoneOrEmail.isNotEmpty()) {
            binding.registerTextView.text = "Почта"
            binding.phoneEmailEditText.inputType =
                android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        } else {
            binding.registerTextView.text = "Телефон"
            binding.phoneEmailEditText.inputType = android.text.InputType.TYPE_CLASS_PHONE
            val listener =
                MaskedTextChangedListener("+7([000])-[000]-[00]-[00]", binding.phoneEmailEditText)
            binding.phoneEmailEditText.addTextChangedListener(listener)
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.getCodeBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }

    override fun success() {
        showLoading(false)
        findNavController().navigate(
            Registration4FragmentDirections.actionRegistration4FragmentToRegistration5Fragment(
                binding.phoneEmailEditText.text.toString()
            )
        )

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
        showLoading(true)
    }

}