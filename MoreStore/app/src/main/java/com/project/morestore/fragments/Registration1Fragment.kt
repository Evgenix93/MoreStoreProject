package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration1Binding
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.presenters.AuthPresenter
import com.project.morestore.util.isEmailValid
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Registration1Fragment : MvpAppCompatFragment(R.layout.fragment_registration1), AuthMvpView {
    private val binding: FragmentRegistration1Binding by viewBinding()
    private val presenter by moxyPresenter { AuthPresenter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
    }

    private fun setClickListeners() {
        binding.getCodeBtn.setOnClickListener {
            presenter.phoneRegister1(binding.phoneEmailEditText.text.toString())
        }

    }

    private fun initToolbar() {
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }


    override fun success(result: Any) {
        findNavController().navigate(
            Registration1FragmentDirections.actionRegistration1FragmentToRegistration2Fragment(
                binding.phoneEmailEditText.text.toString(),
                (result as RegistrationResponse).user?.toInt()!!
            )
        )
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
        Log.d("mylog", "loading")

    }
}