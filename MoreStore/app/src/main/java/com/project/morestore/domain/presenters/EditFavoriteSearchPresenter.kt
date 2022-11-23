package com.project.morestore.domain.presenters

import com.project.morestore.data.models.FavoriteSearchValue
import com.project.morestore.data.models.Filter
import com.project.morestore.data.models.Id
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.presentation.mvpviews.EditFavoriteSearchView
import com.project.morestore.presentation.mvpviews.FavoritesMvpView
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class EditFavoriteSearchPresenter @Inject constructor(
    private val userRepository: UserRepository
): MvpPresenter<EditFavoriteSearchView>() {

    fun reserveFilter(){
        userRepository.reserveFilter()
    }

    fun restoreFilter(){
        userRepository.restoreFilter()
    }

    fun getFilter(){
        viewState.loaded(listOf(userRepository.getFilter()))
    }

    fun getFavoriteSearchById(id: Long){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getFavoriteSearchById(id)
            when (response?.code()) {
                200 -> {
                    viewState.loaded(listOf(response.body()!!))

                }
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()

                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun updateFilter(filter: Filter){
        userRepository.updateFilter(filter)
    }

    fun saveFavoriteSearch(filter: Filter) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.saveFavoriteSearch(FavoriteSearchValue(value = filter))
            when (response?.code()) {
                200 -> {
                    viewState.success()
                }
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun editFavoriteSearch(id: Long, filter: Filter){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.editFavoriteSearch(FavoriteSearchValue(id, filter))
            when (response?.code()) {
                200 -> {
                    viewState.success()


                }
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun deleteFavoriteSearch(id: Long){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.deleteFavoriteSearch(Id(id))
            when (response?.code()) {
                200 -> {
                    viewState.success()
                }
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }
}