package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration1Binding
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.presenters.AuthPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class RegistrationLogin1Fragment : MvpAppCompatFragment(R.layout.fragment_registration1), AuthMvpView {
    private val binding: FragmentRegistration1Binding by viewBinding()
    private val presenter by moxyPresenter { AuthPresenter(requireContext()) }
    private val args: RegistrationLogin1FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
    }

    private fun setClickListeners() {
        binding.getCodeBtn.setOnClickListener {
            val isEmail = binding.phoneEmailEditText.text.toString().contains(Regex("[a-z]"))
            if (!args.isLogin) {
                if (isEmail) {
                    presenter.register(
                        email = binding.phoneEmailEditText.text.toString(),
                        step = 1,
                        type = 2
                    )
                } else {
                    presenter.register(
                        phone = binding.phoneEmailEditText.text.toString(),
                        step = 1,
                        type = 1
                    )
                }
            } else {
                if (isEmail) {
                    presenter.login(
                        email = binding.phoneEmailEditText.text.toString(),
                        step = 1,
                        type = 2
                    )
                } else {
                    presenter.login(
                        phone = binding.phoneEmailEditText.text.toString(),
                        step = 1,
                        type = 1
                    )
                }
            }
        }

    }

    private fun initToolbar() {
        with(binding.toolbar) {
            backIcon.setOnClickListener { findNavController().popBackStack() }
            titleTextView.text = if (args.isLogin) "Вход" else "Регистрация"
        }
    }

    private fun showLoading(loading: Boolean){
        binding.getCodeBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }


    override fun success(result: Any) {
        showLoading(false)
        findNavController().navigate(
            RegistrationLogin1FragmentDirections.actionRegistration1FragmentToRegistration2Fragment(
                binding.phoneEmailEditText.text.toString(),
                (result as RegistrationResponse).user?.toInt()!!,
                args.isLogin
            )
        )
    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
        Log.d("mylog", "loading")
        showLoading(true)

    }
}