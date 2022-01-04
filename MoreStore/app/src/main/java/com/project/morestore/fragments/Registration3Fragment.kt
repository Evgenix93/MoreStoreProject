package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration3Binding
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.presenters.AuthPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Registration3Fragment : MvpAppCompatFragment(R.layout.fragment_registration3), AuthMvpView {
    private val binding: FragmentRegistration3Binding by viewBinding()
    private val args: Registration3FragmentArgs by navArgs()
    private val presenter by moxyPresenter { AuthPresenter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
    }

    private fun setClickListeners() {
        binding.nextBtn.setOnClickListener {
            presenter.phoneRegister3(
                code = args.code,
                userId = args.userId,
                name = binding.nameEditText.text.toString(),
                surname = binding.surnameEditText.text.toString()
            )


        }
    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Личные данные"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    override fun success(result: Any) {
        findNavController().navigate(
            Registration3FragmentDirections.actionRegistration3FragmentToRegistration4Fragment(
                args.phoneOrEmail
            )
        )

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()

    }

    override fun loading() {

    }
}