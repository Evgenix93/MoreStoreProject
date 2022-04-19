package com.project.morestore.presenters

import com.project.morestore.apis.BrandApi
import com.project.morestore.models.NewBrand
import com.project.morestore.mvpviews.NewBrandView
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class NewBrandPresenter(
    private val network :BrandApi
) : MvpPresenter<NewBrandView>(){

    fun createBrand(name :String){
        presenterScope.launch {
            viewState.loading()
            network.addBrand(NewBrand(name))
            viewState.loading(false)
            viewState.finish(name)
        }
    }

}