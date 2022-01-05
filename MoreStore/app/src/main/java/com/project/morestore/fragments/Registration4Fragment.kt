package com.project.morestore.fragments

import android.icu.text.DisplayContext
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter


class Registration4Fragment : MvpAppCompatFragment(R.layout.fragment_registration1), UserMvpView {
    private val binding: FragmentRegistration1Binding by viewBinding()
    private val args: Registration4FragmentArgs by navArgs()
    private val presenter by moxyPresenter { UserPresenter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
        initEditText()


    }

    private fun setClickListeners() {
        val isEmail = args.phoneOrEmail.contains(Regex("[a-z]"))
        binding.getCodeBtn.setOnClickListener {
            if (!isEmail) {
                presenter.changeUserData(email = binding.phoneEmailEditText.text.toString())
            }else{
                presenter.changeUserData(phone = binding.phoneEmailEditText.text.toString())
            }
        }
    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Последний шаг"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initEditText() {
        if (!args.phoneOrEmail.contains(Regex("[a-z]")) || args.phoneOrEmail.isEmpty()) {
            binding.registerTextView.text = "Почта"
            binding.phoneEmailEditText.inputType =
                android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            //val slots = ("___@___")
            //val formatWatcher = MaskFormatWatcher(MaskImpl.createNonTerminated(slots))
            //formatWatcher.installOn(binding.phoneEmailEditText)

            //val listener = MaskedTextChangedListener("[…]@[AAA]", binding.phoneEmailEditText)
            //binding.phoneEmailEditText.addTextChangedListener(listener)
            //binding.phoneEmailEditText.onFocusChangeListener = listener
        } else {
            binding.registerTextView.text = "Телефон"
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

    override fun success() {
        findNavController().navigate(
            Registration4FragmentDirections.actionRegistration4FragmentToRegistration5Fragment(
                binding.phoneEmailEditText.text.toString()
            )
        )

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {

    }

}