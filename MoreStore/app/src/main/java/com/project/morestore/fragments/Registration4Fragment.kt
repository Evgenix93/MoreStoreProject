package com.project.morestore.fragments

import android.icu.text.DisplayContext
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
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
import com.redmadrobot.inputmask.MaskedTextChangedListener


class Registration4Fragment : Fragment(R.layout.fragment_registration1) {
    private val binding: FragmentRegistration1Binding by viewBinding()
    private val args: Registration4FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()

            if (!args.phoneOrEmail.contains(Regex("[a-z]")) || args.phoneOrEmail.isEmpty()) {
                binding.registerTextView.text = "Почта"
                binding.phoneEmailEditText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                //val slots = ("___@___")
                //val formatWatcher = MaskFormatWatcher(MaskImpl.createNonTerminated(slots))
                //formatWatcher.installOn(binding.phoneEmailEditText)

                //val listener = MaskedTextChangedListener("[…]@[AAA]", binding.phoneEmailEditText)
                //binding.phoneEmailEditText.addTextChangedListener(listener)
                //binding.phoneEmailEditText.onFocusChangeListener = listener




            } else {
                binding.registerTextView.text = "Телефон"
                binding.phoneEmailEditText.inputType = android.text.InputType.TYPE_CLASS_PHONE
                val listener = MaskedTextChangedListener("+7([000])-[000]-[00]-[00]", binding.phoneEmailEditText)
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

    private fun setClickListeners() {
        binding.getCodeBtn.setOnClickListener {
            findNavController().navigate(
                Registration4FragmentDirections.actionRegistration4FragmentToRegistration2Fragment(
                    binding.phoneEmailEditText.text.toString()
                )
            )
        }
    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Последний шаг"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

}