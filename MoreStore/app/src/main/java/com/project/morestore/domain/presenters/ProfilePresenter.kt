package com.project.morestore.domain.presenters

import android.util.Log
import com.project.morestore.data.models.Card
import com.project.morestore.data.repositories.CardRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.presentation.mvpviews.ProfileMvpView
import com.project.morestore.util.errorMessage
import com.project.morestore.util.getStringFromResponse
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class ProfilePresenter @Inject constructor(private val userRepository: UserRepository,
                                           private val cardRepository: CardRepository): MvpPresenter<ProfileMvpView>() {

    fun getCurrentUserAddress() {
        val currentAddress = userRepository.getCurrentUserAddress()
        if (currentAddress != null)
            viewState.loaded(currentAddress)
    }

    fun loadOnboardingData() {
        presenterScope.launch {
            val response = userRepository.loadBrandsProperties()
            when (response?.code()) {
                200 -> {
                    Log.d("MyDebug", "load brandsPropert success")
                    viewState.loaded(response.body()!!)
                }
                else -> {
                    viewState.error(errorMessage(response))
                }
            }
        }
    }

    fun chooseCard(cards: List<Card>){
        viewState.loading()
        presenterScope.launch{
            cards.forEach{
                val deleteResponse = cardRepository.deleteCard(it)
                when(deleteResponse?.code()){
                    404 -> {viewState.error(deleteResponse.errorBody()!!.getStringFromResponse())
                        return@launch}
                    else -> {
                        viewState.error(errorMessage(deleteResponse))
                        return@launch
                    }
                }
            }
            cards.forEach{
                val addResponse = cardRepository.addCard(Card(null, it.number, it.active))
                when(addResponse?.code()){
                    404 -> {viewState.error(addResponse.errorBody()!!.getStringFromResponse())
                        return@launch}
                    else -> {
                        viewState.error(errorMessage(addResponse))
                        return@launch
                    }
                }
            }
            getCards()
        }
    }

    fun getCards(){
        viewState.loading()
        presenterScope.launch {
            val response = cardRepository.getCards()
            when(response?.code()){
                200 -> {
                    viewState.loaded(response.body()!!)
                }
                404 -> viewState.loaded(listOf<Card>())
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun deleteCard(card: Card){
        viewState.loading()
        presenterScope.launch{
            val response = cardRepository.deleteCard(card)
            when(response?.code()){
                200 -> {
                    getCards()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

}