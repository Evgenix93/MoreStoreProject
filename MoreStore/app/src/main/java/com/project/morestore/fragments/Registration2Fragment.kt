package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration2Binding
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.presenters.AuthPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Registration2Fragment : MvpAppCompatFragment(R.layout.fragment_registration2), AuthMvpView {
    private val binding: FragmentRegistration2Binding by viewBinding()
    private val args: Registration2FragmentArgs by navArgs()
    private val presenter by moxyPresenter { AuthPresenter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()

        if (args.phoneOrEmail.contains(Regex("[a-z]"))) {
            binding.textView2.text = "В течении 2 минут вы получите письмо с кодом"
            binding.textView3.text = "подтверждения на почту"
            binding.phoneEmailTextView.text = "irina.ivanova@mail.ru"
        }
    }

    private fun setClickListeners() {
        binding.confirmBtn.setOnClickListener {
            val navController = findNavController()
            if (navController.previousBackStackEntry?.destination?.id == R.id.registration4Fragment) {
                navController.navigate(Registration2FragmentDirections.actionRegistration2FragmentToOnboarding1Fragment())

            } else {
                presenter.emailRegister2(args.user, binding.codeEditText.text.toString().toInt())
            }

        }


    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Подтверждение номера"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    override fun success(response: Any) {
        val registrationResponse = response as RegistrationResponse
        findNavController().navigate(
            Registration2FragmentDirections.actionRegistration2FragmentToRegistration3Fragment(
              args.phoneOrEmail,
              registrationResponse.user!!,
              registrationResponse.code!!
            )
        )
    }

    override fun loading() {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
        TODO("Not yet implemented")
    }
}