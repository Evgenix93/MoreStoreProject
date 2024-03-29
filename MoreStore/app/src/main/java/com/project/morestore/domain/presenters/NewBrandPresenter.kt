package com.project.morestore.domain.presenters

import com.project.morestore.data.apis.BrandApi
import com.project.morestore.data.models.NewBrand
import com.project.morestore.presentation.mvpviews.NewBrandView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class NewBrandPresenter @Inject constructor(
    private val network :BrandApi
) : MvpPresenter<NewBrandView>(){

    fun createBrand(name :String){
        presenterScope.launch {
            viewState.loading()
            val response = try {

                network.addBrand(NewBrand(name = name))
            }catch (e: Throwable){
                if(e is IOException)
                    null
                else Response.error(400, "ошибка".toResponseBody())
            }
            viewState.loading(false)
            when(response?.code()){
                200 -> viewState.finish(response.body()!!.toLong(), name)
                else -> viewState.showMessage(errorMessage(response))
            }
        }
    }
}