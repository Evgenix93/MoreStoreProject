package com.project.morestore.presenters

import com.project.morestore.models.Card
import com.project.morestore.mvpviews.AddCardMvpView
import com.project.morestore.mvpviews.MainFragmentMvpView
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.repositories.CardRepository
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class AddCardPresenter @Inject constructor(private val cardRepository: CardRepository): MvpPresenter<AddCardMvpView>() {

    fun addCard(cardNumber: String){
        presenterScope.launch {
            if (!checkCardNumber(cardNumber)) {
                viewState.error("Введите правильный номер карты")
                return@launch
            }

            val cardsResponse = cardRepository.getCards()
            when(cardsResponse?.code()){
                200 -> {
                    val cards = cardsResponse.body()!!

                    cards.forEach{
                        val deleteResponse = cardRepository.deleteCard(it)
                        if(deleteResponse?.code() != 200) {
                            viewState.error(errorMessage(deleteResponse))
                            return@launch
                        }
                        val addResponse = cardRepository.addCard(Card(id = null, number = it.number, active = 0))
                        if(addResponse?.code() != 200) {
                            viewState.error(errorMessage(deleteResponse))
                            return@launch
                        }
                    }
                }
                else -> viewState.error(errorMessage(cardsResponse))
            }
            val response = cardRepository.addCard(Card(id = null, number = cardNumber, active = 1))
            when(response?.code()){
                200 -> if(response.body()!!.contains("error")) viewState.error(response.body()!!)
                else (viewState as MainFragmentMvpView).success()
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    private fun checkCardNumber(cardNumber: String): Boolean{
        return cardNumber.isNotEmpty() && cardNumber.filter { it != '-' }.length == 16
    }
}