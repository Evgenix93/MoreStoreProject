package com.project.morestore.presenters

import com.project.morestore.apis.BrandApi
import com.project.morestore.models.NewBrand
import com.project.morestore.mvpviews.NewBrandView
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class NewBrandPresenter(
    private val network :BrandApi
) : MvpPresenter<NewBrandView>(){

    fun createBrand(name :String){
        presenterScope.launch {
            viewState.loading()
            val response = try {

                network.addBrand(NewBrand(name = name, status = 1))
            }catch (e: Throwable){
                if(e is IOException)
                    null
                else Response.error(400, "ошибка".toResponseBody())
            }
            viewState.loading(false)
            when(response?.code()){
                200 -> viewState.finish(name, response.body()?.id!!)
                400 -> viewState.showMessage("ошибка")
                null -> viewState.showMessage("нет интернета")
            }


        }
    }

}