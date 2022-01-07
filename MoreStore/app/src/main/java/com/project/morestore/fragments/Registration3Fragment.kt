package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration3Binding

import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.presenters.AuthPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File

class Registration3Fragment : MvpAppCompatFragment(R.layout.fragment_registration3), AuthMvpView {
    private val args: Registration3FragmentArgs by navArgs()
    private val presenter by moxyPresenter { AuthPresenter(requireContext()) }
    private val binding: FragmentRegistration3Binding by viewBinding()
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
        initFilePickerLauncher()
    }

    private fun setClickListeners() {
     /*   val isEmail = args.phoneOrEmail.contains(Regex("[a-z]"))
        binding.nextBtn.setOnClickListener {
            presenter.register(
                code = args.code,
                user = args.userId,
                name = binding.nameEditText.text.toString(),
                surname = binding.surnameEditText.text.toString(),
                step = 3,
                type = if(isEmail) 2 else 1
            )
        }*/

        binding.photoImageView.setOnClickListener{
            filePickerLauncher.launch(arrayOf("image/*"))
        }
    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Личные данные"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) {uri ->
            Glide.with(binding.root)
                .load(uri)
                .into(binding.photoImageView)
            binding.photoImageView.strokeColor = ColorStateList.valueOf(resources.getColor(R.color.green))
            binding.photoImageView.strokeWidth = 10f
            presenter.safePhotoUri(uri)
        }
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