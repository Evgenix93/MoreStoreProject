package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration2Binding
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Registration5Fragment : MvpAppCompatFragment(R.layout.fragment_registration2), UserMvpView {
    private val binding: FragmentRegistration2Binding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter() }
    private val args: Registration5FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        setClickListeners()
    }


    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Подтверждение номера"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setClickListeners() {
        binding.confirmBtn.setOnClickListener {
            if (!args.phoneOrEmail.contains(Regex("[a-z]"))) {
                presenter.changeUserData(
                    phone = args.phoneOrEmail,
                    step = 2,
                    code = binding.codeEditText.text.toString().toInt()
                )
            }else{
                presenter.changeUserData(
                    email = args.phoneOrEmail,
                    step = 2,
                    code = binding.codeEditText.text.toString().toInt()
                )

            }

        }
    }


    override fun success() {
        findNavController().navigate(Registration5FragmentDirections.actionRegistration5FragmentToOnboarding1Fragment())

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {

    }
}