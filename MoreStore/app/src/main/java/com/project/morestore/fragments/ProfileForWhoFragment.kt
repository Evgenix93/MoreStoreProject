package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentProfileForWhoBinding
import com.project.morestore.data.models.BrandsPropertiesDataWrapper
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.presenters.OnboardingPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class ProfileForWhoFragment: MvpAppCompatFragment(R.layout.fragment_profile_for_who), OnBoardingMvpView {
    private val binding: FragmentProfileForWhoBinding by viewBinding()
    @Inject
    lateinit var onboardingPresenter: OnboardingPresenter
    private val presenter: OnboardingPresenter by moxyPresenter { onboardingPresenter }
    private lateinit var brandsPropertiesDataWrapper: BrandsPropertiesDataWrapper

    private fun initCheckButtons(){
        val propertiesId = brandsPropertiesDataWrapper.data.property?.split(';')
            ?.mapNotNull{it.toLongOrNull()} ?: return
        binding.forWomenCheckBox.isChecked = propertiesId.any{it == 140L}
        binding.forMenCheckBox.isChecked = propertiesId.any{it == 141L}
        binding.forKidsCheckBox.isChecked = propertiesId.any{it == 142L}
        }

    private fun saveForWho(){
        val propertiesId = mutableListOf<Long>().apply{
            with(binding){
                if(forWomenCheckBox.isChecked)
                    add(140L)
                if(forMenCheckBox.isChecked)
                    add(141L)
                if(forKidsCheckBox.isChecked)
                    add(142L)
                addAll(brandsPropertiesDataWrapper.data.property?.split(';')
                    ?.mapNotNull{it.toLongOrNull()}?.filter{it != 140L && it!= 141L && it != 142L}.orEmpty().also {
                        Log.d("MyDebug", "addAll = $it")
                    })

            }
        }.also{Log.d("MyDebug", "propertiesId = $it")}
        val brandsId = brandsPropertiesDataWrapper.data.brand?.split(';')
            ?.mapNotNull { it.toLongOrNull() }
        presenter.saveOnBoardingData(brandsId, propertiesId)
    }

    private fun loadOnBoardingData(){
        presenter.loadOnboardingData()
    }

    private fun initSaveButton(){
        binding.saveButton.setOnClickListener{
            saveForWho()
        }
    }

    private fun initToolbar(){
        with(binding.toolbar) {
            actionTextView.isVisible = false
            titleTextView.text = "Кому ищите товары"
            arrowBackImageView.setOnClickListener { findNavController().popBackStack() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        loadOnBoardingData()
    }

    override fun loading() {
        binding.loader.isVisible = true
    }

    override fun loaded(result: List<Any>) {
        binding.loader.isVisible = false
        brandsPropertiesDataWrapper = result.last() as BrandsPropertiesDataWrapper
        initCheckButtons()
        initSaveButton()
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun success() {
        findNavController().popBackStack()
    }
}