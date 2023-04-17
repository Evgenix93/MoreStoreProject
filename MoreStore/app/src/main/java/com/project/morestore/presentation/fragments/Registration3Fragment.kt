package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration3Binding
import com.project.morestore.domain.presenters.RegistrationPresenter

import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.presentation.mvpviews.RegistrationMvpView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class Registration3Fragment : MvpAppCompatFragment(R.layout.fragment_registration3), RegistrationMvpView {
    private val args: Registration3FragmentArgs by navArgs()
    @Inject
    lateinit var registrationPresenter: RegistrationPresenter
    private val presenter by moxyPresenter { registrationPresenter }
    private val binding: FragmentRegistration3Binding by viewBinding()
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSoftInputMode()
        setClickListeners()
        initToolbar()
        initFilePickerLauncher()
        hideBottomNavigation()
    }

    private fun setSoftInputMode(){
        (activity as MainActivity).changeSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

    }

    private fun setClickListeners() {
        binding.nextBtn.setOnClickListener {
            presenter.changeUserData(name = binding.nameEditText.text.toString(), surname = binding.surnameEditText.text.toString())
        }

        binding.photoImageView.setOnClickListener{
            filePickerLauncher.launch(arrayOf("image/*"))
        }
    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Личные данные"
        binding.toolbar.backIcon.setOnClickListener {
            if(args.fromMainFragment)
                findNavController().navigate(Registration3FragmentDirections.actionRegistration3FragmentToFirstLaunchFragment(),
                NavOptions.Builder().setPopUpTo(R.id.splashScreenFragment, false).build())
            else
                findNavController().popBackStack()
        }
    }

    private fun initFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) {uri ->
            uri ?: return@registerForActivityResult
            Glide.with(binding.root)
                .load(uri)
                .into(binding.photoImageView)
            binding.photoImageView.strokeColor = ColorStateList.valueOf(resources.getColor(R.color.green))
            binding.photoImageView.strokeWidth = 10f
            if (uri != null)
            presenter.safePhotoUri(uri)
        }
    }

    private fun showLoading(loading: Boolean){
        binding.nextBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }

    private fun hideBottomNavigation(){
        (activity as MainActivity).showBottomNavBar(false)
    }

    override fun success() {
        showLoading(false)
        findNavController().navigate(
            Registration3FragmentDirections.actionRegistration3FragmentToRegistration4Fragment(
                args.phoneOrEmail
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

}