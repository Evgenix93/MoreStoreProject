package com.project.morestore.fragments

import android.icu.text.DisplayContext
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration1Binding
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.lang.reflect.Type
import java.sql.Types
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import com.project.morestore.MainActivity
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter


class Registration4Fragment : MvpAppCompatFragment(R.layout.fragment_registration1), UserMvpView {
    private val binding: FragmentRegistration1Binding by viewBinding()
    private val args: Registration4FragmentArgs by navArgs()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }


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
                    Registration4FragmentDirections.actionRegistration4FragmentToMainFragment()
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
        binding.toolbar.titleTextView.text = "?????????????????? ??????"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initEditText() {
        if (!args.phoneOrEmail.contains(Regex("[a-z]")) && args.phoneOrEmail.isNotEmpty()) {
            binding.registerTextView.text = "??????????"
            binding.phoneEmailEditText.inputType =
                android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            //val slots = ("___@___")
            //val formatWatcher = MaskFormatWatcher(MaskImpl.createNonTerminated(slots))
            //formatWatcher.installOn(binding.phoneEmailEditText)

            //val listener = MaskedTextChangedListener("[???]@[AAA]", binding.phoneEmailEditText)
            //binding.phoneEmailEditText.addTextChangedListener(listener)
            //binding.phoneEmailEditText.onFocusChangeListener = listener
        } else {
            binding.registerTextView.text = "??????????????"
            binding.phoneEmailEditText.inputType = android.text.InputType.TYPE_CLASS_PHONE
            val listener =
                MaskedTextChangedListener("+7([000])-[000]-[00]-[00]", binding.phoneEmailEditText)
            binding.phoneEmailEditText.addTextChangedListener(listener)
            //val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
            //val watcher = MaskFormatWatcher(mask)
            //watcher.installOn(binding.phoneEmailEditText)
        }

        //binding.phoneEmailEditText.setOnFocusChangeListener { view, b ->
        // binding.phoneEmailEditText.setMask("+7(###)-###-##-##")
        // Log.d("mylog", "onFocusChange")

        //}

    }

    private fun showLoading(loading: Boolean) {
        binding.getCodeBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }

    override fun success(result: Any) {
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

    override fun loaded(result: Any) {
        TODO("Not yet implemented")
    }

    override fun successNewCode() {

    }

}