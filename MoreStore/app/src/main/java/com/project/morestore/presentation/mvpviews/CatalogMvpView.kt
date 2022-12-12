package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.SuggestionModels
import moxy.viewstate.strategy.alias.OneExecution

interface CatalogMvpView: LoadingMvpView, ResultLoadedMvpView {

    @OneExecution
    fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>)

}