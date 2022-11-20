package com.project.morestore.fragments.orders.problems

import android.content.Context
import com.project.morestore.dialogs.ProblemTypeDialog
import com.project.morestore.models.ProductProblemsData
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class OrderProblemsPresenter(context: Context) : MvpPresenter<OrderProblemsView>() {

    private val userRepository = UserRepository(context)
    private var productProblemsData = ProductProblemsData();


    override fun attachView(view: OrderProblemsView?) {
        super.attachView(view)
        getUserInfo()
    }

    fun onNextClick(productId: Long) {
        productProblemsData.idProduct = productId
        viewState.openProblemsPhotosPage(productProblemsData);
    }

    fun reasonOnClick() {
        val problemDialog = ProblemTypeDialog(object : ProblemTypeDialog.onClickListener {
            override fun onClick(type: String) {
                productProblemsData.problem = type;
                viewState.setProductData(productProblemsData);
            }
        })
        viewState.showReasonDialog(problemDialog);
    }

    fun onCommentChange(comment: String) {
        productProblemsData.comment = comment
        viewState.setProductData(productProblemsData)
    }

    fun onPhoneChanged(phone: String?) {
        productProblemsData.phone = phone;
        viewState.setProductData(productProblemsData)
    }

    private fun getUserInfo() {
        presenterScope.launch {
            val response = userRepository.getCurrentUserInfo()
            when (response?.code()) {
                200 -> {
                    productProblemsData.phone = response.body()?.phone
                    productProblemsData.phone?.let { viewState.setPhone(it) }
                }
            }
        }
    }
}