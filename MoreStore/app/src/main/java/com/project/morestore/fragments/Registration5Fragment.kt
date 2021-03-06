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
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Registration5Fragment : MvpAppCompatFragment(R.layout.fragment_registration2), UserMvpView {
    private val binding: FragmentRegistration2Binding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private val args: Registration5FragmentArgs by navArgs()
    private var isEmail = false
    private lateinit var timer: CountDownTimer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isEmail =  args.phoneOrEmail.contains(Regex("[a-z]"))
        initToolbar()
        initCounter()
        initText()
        setClickListeners()

    }


    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Подтверждение номера"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setClickListeners() {
        binding.confirmBtn.setOnClickListener {
            if (!isEmail) {
                presenter.changeUserData2(
                    phone = args.phoneOrEmail,
                    step = 2,
                    code = binding.codeEditText.text.toString().toIntOrNull()
                )
            }else{
                presenter.changeUserData2(
                    email = args.phoneOrEmail,
                    step = 2,
                    code = binding.codeEditText.text.toString().toIntOrNull()
                )

            }

        }

        binding.getNewCodeTextView.setOnClickListener {
            if (isEmail) {
                presenter.getNewCode(email = args.phoneOrEmail)
            } else {
                presenter.getNewCode(phone = args.phoneOrEmail)
            }

        }
    }

    private fun initCounter() {
        timer = object : CountDownTimer(120000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.timerTextView.text =
                    "${millisUntilFinished / 60000}:${millisUntilFinished / 1000 - (millisUntilFinished / 60000) * 60}"
            }

            override fun onFinish() {
                binding.getNewCodeTextView.isVisible = true
                binding.textView4.isVisible = false
                binding.timerTextView.isVisible = false
            }
        }.start()
    }

    private fun initText() {
        if (isEmail) {
            binding.textView2.text = "В течении 2 минут вы получите письмо с кодом"
            binding.textView3.text = "подтверждения на почту"
            binding.phoneEmailTextView.text = args.phoneOrEmail
        } else {
            binding.textView2.text = "В течении 2 минут вы получите смс с кодом"
            binding.textView3.text = "подтверждения на номер"
            binding.phoneEmailTextView.text = args.phoneOrEmail
        }
    }

    private fun showLoading(loading: Boolean){
        binding.confirmBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }


    override fun success(result: Any) {
        showLoading(false)
        findNavController().navigate(Registration5FragmentDirections.actionRegistration5FragmentToOnboarding1Fragment())

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {
        showLoading(true)

    }

    override fun loaded(result: Any) {
        TODO("Not yet implemented")
    }

    override fun successNewCode() {
        showLoading(false)
        binding.getNewCodeTextView.isVisible = false
        binding.textView4.isVisible = true
        binding.timerTextView.isVisible = true
        initCounter()
    }
}