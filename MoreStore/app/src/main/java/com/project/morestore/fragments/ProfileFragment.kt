package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentProfileBinding
import com.project.morestore.models.Address
import com.project.morestore.models.BrandsPropertiesDataWrapper
import com.project.morestore.models.Region
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ProfileFragment: MvpAppCompatFragment(R.layout.fragment_profile), UserMvpView {
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private val binding: FragmentProfileBinding by viewBinding()
    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var brandsPropertiesDataWrapper: BrandsPropertiesDataWrapper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        getUser()
        setClickListeners()
        loadOnBoardingData()
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Профиль"
        binding.toolbar.backIcon.setOnClickListener{findNavController().popBackStack()}
    }

    private fun getUser(){
        //presenter.getCurrentRegion()
        presenter.getCurrentUserAddress()
        val listener =
            MaskedTextChangedListener("+7([000])-[000]-[00]-[00]", binding.phoneEditText)
        binding.phoneEditText.addTextChangedListener(listener)
        binding.phoneEditText.setText(args.user.phone)
        binding.nameTextView.text = "${args.user.name} ${args.user.surname}"
        binding.emailTextView.text = args.user.email
        Glide.with(binding.root)
            .load(args.user.avatar?.photo)
            .into(binding.avatarImageView)
    }

    private fun setClickListeners(){
        binding.draftTextView.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProductDraftFragment())
        }
        binding.favoriteTextView.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFavoritesFragment())
        }
        binding.changeAddressTextView.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToChangeRegionFragment(isForFilter = false))
        }
        binding.mySizesTextView.setOnClickListener{
           findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToOnboarding2Fragment(isMale = true, fromProfile = true))
        }
        binding.myBrandsTextView.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToOnboarding3Fragment(isMale = true, fromProfile = true))
        }
        binding.forWhoTextView.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileForWhoFragment())
        }

         binding.waitReviewSellersTextView.setOnClickListener{
             findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToWaitReviewSellersFragment())
        }

        binding.blackListTextView.setOnClickListener{
            findNavController().navigate(R.id.blackListFragment)
        }
    }

    private fun loadOnBoardingData(){
        presenter.loadOnboardingData()
    }

    override fun success(result: Any) {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun loading() {
        TODO("Not yet implemented")
    }

    override fun loaded(result: Any) {
            if(result is List<*>){
              brandsPropertiesDataWrapper = (result as List<BrandsPropertiesDataWrapper>).last()
            }
        else {
                val currentAddress = result as Address
                binding.currentRegionTextView.text = currentAddress.fullAddress?.substringBefore(",")
            }
    }

    override fun successNewCode() {
        TODO("Not yet implemented")
    }
}