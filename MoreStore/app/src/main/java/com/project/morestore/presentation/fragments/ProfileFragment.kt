package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.presentation.adapters.CardsAdapter
import com.project.morestore.databinding.FragmentProfileBinding
import com.project.morestore.data.models.Address
import com.project.morestore.data.models.BrandsPropertiesDataWrapper
import com.project.morestore.data.models.Card
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment: MvpAppCompatFragment(R.layout.fragment_profile), UserMvpView {
    @Inject
    lateinit var userPresenter: UserPresenter
    private val presenter by moxyPresenter { userPresenter }
    private val binding: FragmentProfileBinding by viewBinding()
    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var brandsPropertiesDataWrapper: BrandsPropertiesDataWrapper
    private var cardsAdapter: CardsAdapter by autoCleared()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        getUser()
        setClickListeners()
        loadOnBoardingData()
        initCardList()
        getCards()
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Профиль"
        binding.toolbar.backIcon.setOnClickListener{findNavController().popBackStack()}
    }

    private fun getUser(){
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

        binding.addCardTextView.setOnClickListener{
            findNavController().navigate(R.id.addCardFragment)
        }

    }

    private fun loadOnBoardingData(){
        presenter.loadOnboardingData()
    }

   private fun initCardList(){
        cardsAdapter = CardsAdapter ({cards ->
        presenter.chooseCard(cards)
        }, {deleteCard(it)})
        binding.cardsRecyclerView.apply {
            adapter = cardsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getCards(){
        presenter.getCards()
    }

    private fun deleteCard(card: Card){
        presenter.deleteCard(card)
    }

    override fun success(result: Any) {
    }

    override fun error(message: String) {
        binding.loaderProgressBar.isVisible = false
        cardsAdapter.loading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun loading() {
        binding.loaderProgressBar.isVisible = true
        cardsAdapter.loading(true)
    }

    override fun loaded(result: Any) {
        binding.loaderProgressBar.isVisible = false
        cardsAdapter.loading(false)
            if(result is List<*>){
              if(result.isNotEmpty() && result.first() is BrandsPropertiesDataWrapper)
              brandsPropertiesDataWrapper = (result as List<BrandsPropertiesDataWrapper>).last()
              else
                  cardsAdapter.updateCards(result as List<Card>)
            }
        else {
                val currentAddress = result as Address
                binding.currentRegionTextView.text = currentAddress.fullAddress?.substringBefore(",")
            }
    }
}