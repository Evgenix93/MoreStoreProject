package com.project.morestore.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration1Binding
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.presenters.AuthPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Registration1Fragment: MvpAppCompatFragment(R.layout.fragment_registration1), AuthMvpView  {
    private val binding: FragmentRegistration1Binding by viewBinding()
    private val presenter by moxyPresenter { AuthPresenter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()

    }

    private fun setClickListeners(){
        binding.getCodeBtn.setOnClickListener {
          presenter.emailRegister1(binding.phoneEmailEditText.text.toString())
        }

    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initKeyBoard(){
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.phoneEmailEditText.setOnEditorActionListener { textView, i, keyEvent ->
            true
        }

    }

    override fun success(response: Any) {
       val registrationResponse = response as RegistrationResponse
       findNavController().navigate(
           Registration1FragmentDirections.actionRegistration1FragmentToRegistration2Fragment(
            binding.phoneEmailEditText.text.toString(),
            registrationResponse.user!!
           )
       )
    }

    override fun loading() {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
     Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}