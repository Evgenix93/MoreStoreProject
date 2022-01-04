package com.project.morestore.presenters

import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.repositories.AuthRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class AuthPresenter: MvpPresenter<AuthMvpView>() {
   private val repository = AuthRepository()

   fun emailRegister1(email: String){
       presenterScope.launch {
         val response = repository.emailRegister1(email)
           if (response == null)
               viewState.error("Нет сети")
           else{
              when (response.code()){
                  200 -> viewState.success(response.body()!!)
                  else -> viewState.error("Ошибка регистрации")
              }
           }
       }
   }

   fun emailRegister2(user: Int, code: Int){
       presenterScope.launch {
           val response = repository.emailRegister2(user, code)
           if (response == null)
               viewState.error("Нет сети")
           else{
               when (response.code()){
                   200 -> viewState.success(response.body()!!)
                   else -> viewState.error("Ошибка регистрации")
               }
           }
       }
   }

   fun emailRegister3(user: Int, code: Int, name:String, surname: String){
       presenterScope.launch {
           val response = repository.emailRegister3(user, code, name, surname)
           if (response == null)
               viewState.error("Нет сети")
           else{
               when (response.code()){
                   200 -> viewState.success(response.body()!!)
                   else -> viewState.error("Ошибка регистрации")
               }
           }
       }
   }
}