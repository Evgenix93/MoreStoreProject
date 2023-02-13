package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.data.models.Address
import com.project.morestore.data.models.BrandsPropertiesDataWrapper
import com.project.morestore.data.models.Card
import com.project.morestore.data.singletones.Token
import com.project.morestore.databinding.FragmentProfileBinding
import com.project.morestore.domain.presenters.ProfilePresenter
import com.project.morestore.presentation.adapters.CardsAdapter
import com.project.morestore.presentation.mvpviews.ProfileMvpView
import com.project.morestore.util.JsObject
import com.project.morestore.util.autoCleared
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment: MvpAppCompatFragment(R.layout.fragment_profile), ProfileMvpView {
    @Inject
    lateinit var userPresenter: ProfilePresenter
    private val presenter by moxyPresenter { userPresenter }
    private val binding: FragmentProfileBinding by viewBinding()
    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var brandsPropertiesDataWrapper: BrandsPropertiesDataWrapper
    private var cardsAdapter: CardsAdapter by autoCleared()
    private var shouldChooseLastCard: Boolean = false


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
            //findNavController().navigate(R.id.addCardFragment)
            openAddCardWebPage()
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

    private fun openAddCardWebPage(){
        binding.webView.isVisible = true
        binding.webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                view?.loadUrl(
                    "javascript:(function() {" +
                            "window.parent.addEventListener ('message', function(event) {" +
                            " Android.receiveMessage(JSON.stringify(event.data));if(JSON.stringify(event.data).includes(\"SUCCESS\")) window.location.href = \"superJessy\";});" +
                            "})()"

                )
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                Log.d("headers", request?.requestHeaders.toString())
                if(request?.url.toString().contains("superJessy") && !shouldChooseLastCard) {
                    Log.d("MyDebugCard", "override url loading")
                    view?.isVisible = false
                    shouldChooseLastCard = true
                    getCards()
                }

                return true
            }


        }

        binding.webView.settings.userAgentString = "Chrome/56.0.0.0 Mobile"
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(JsObject(), "Android")
        binding.webView.loadUrl("https://more.store/ukassa/card?token=${Token.token.substringAfter("Bearer ")}")

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
              else {
                  Log.d("MyDebugCard", "cards test = $result")
                  if(shouldChooseLastCard){
                      (result as List<Card>).forEachIndexed() { index, card ->
                          if(index != result.lastIndex)
                              card.active = 0
                      }
                      presenter.chooseCard(result)
                      shouldChooseLastCard = false
                  }else
                      cardsAdapter.updateCards(result as List<Card>)
              }
            }
        else {
                val currentAddress = result as Address
                binding.currentRegionTextView.text = currentAddress.fullAddress?.substringBefore(",")
            }
    }
}