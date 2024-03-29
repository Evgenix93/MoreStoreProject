package com.project.morestore.domain.presenters

import android.content.Context
import com.project.morestore.R
import com.project.morestore.data.models.Card
import com.project.morestore.presentation.mvpviews.AddCardMvpView
import com.project.morestore.data.repositories.CardRepository
import com.project.morestore.util.errorMessage
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class AddCardPresenter @Inject constructor(@ActivityContext private val context: Context,
                                           private val cardRepository: CardRepository): MvpPresenter<AddCardMvpView>() {

    fun addCard(cardNumber: String){
        presenterScope.launch {
            viewState.loading()
            if (!checkCardNumber(cardNumber)) {
                viewState.error(context.getString(R.string.write_correct_card_number))
                return@launch
            }

            val cardsResponse = cardRepository.getCards()
            when(cardsResponse?.code()){
                200 -> {
                    val cards = cardsResponse.body()!!
                    if(cards.find{it.number == cardNumber} != null) {
                        viewState.error("Такая карта уже добавлена")
                        return@launch
                    }
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
                404 -> {}
                else -> viewState.error(errorMessage(cardsResponse))
            }
            val response = cardRepository.addCard(Card(id = null, number = cardNumber, active = 1))
            when(response?.code()){
                200 -> if(response.body()!!.contains("error")) viewState.error(response.body()!!)
                else viewState.success()
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    private fun checkCardNumber(cardNumber: String): Boolean{
        return cardNumber.isNotEmpty() && cardNumber.filter { it != '-' }.length == 16
    }
}