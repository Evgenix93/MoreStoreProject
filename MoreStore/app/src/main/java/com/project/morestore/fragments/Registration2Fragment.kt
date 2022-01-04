package com.project.morestore.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
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
    private val isEmail = args.phoneOrEmail.contains(Regex("[a-z]"))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
        initText()
        initCounter()


    }

    private fun setClickListeners() {
        binding.confirmBtn.setOnClickListener {
            presenter.phoneRegister2(binding.codeEditText.text.toString().toInt(), args.userId)
        }

        binding.getNewCodeTextView.setOnClickListener {
            if(isEmail){
                presenter.getNewCode(email = args.phoneOrEmail)
            }else{
                presenter.getNewCode(phone = args.phoneOrEmail)
            }
            binding.getNewCodeTextView.isVisible = false
            binding.textView4.isVisible = true
            binding.timerTextView.isVisible = true
            initCounter()
        }


    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Подтверждение номера"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initText(){
        if (isEmail) {
            binding.textView2.text = "В течении 2 минут вы получите письмо с кодом"
            binding.textView3.text = "подтверждения на почту"
            binding.phoneEmailTextView.text = args.phoneOrEmail
        }else{
            binding.textView2.text = "В течении 2 минут вы получите смс с кодом"
            binding.textView3.text = "подтверждения на номер"
            binding.phoneEmailTextView.text = args.phoneOrEmail
        }
    }

    private fun initCounter(){
        object : CountDownTimer(120000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.timerTextView.text = "${millisUntilFinished/60000}:${ millisUntilFinished/1000 - (millisUntilFinished/60000) * 60 }"
            }

            override fun onFinish() {
                binding.getNewCodeTextView.isVisible = true
                binding.textView4.isVisible = false
                binding.timerTextView.isVisible = false
            }
        }.start()
    }

    override fun success(result: Any) {
        val navController = findNavController()
        if (navController.previousBackStackEntry?.destination?.id == R.id.registration4Fragment) {
            navController.navigate(Registration2FragmentDirections.actionRegistration2FragmentToOnboarding1Fragment())

        } else {
            navController.navigate(
                Registration2FragmentDirections.actionRegistration2FragmentToRegistration3Fragment(
                    args.phoneOrEmail,
                    code = (result as RegistrationResponse).code!!,
                    userId = result.user?.toInt()!!
                )
            )
        }


    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {

    }
}