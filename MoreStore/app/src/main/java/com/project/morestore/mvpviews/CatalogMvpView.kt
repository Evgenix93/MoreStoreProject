package com.project.morestore.mvpviews

import com.project.morestore.models.SuggestionModels
import moxy.viewstate.strategy.alias.OneExecution

interface CatalogMvpView: MainMvpView {

    @OneExecution
    fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>)

}