package com.project.morestore.fragments.orders.problems


import com.project.morestore.dialogs.ProblemTypeDialog
import com.project.morestore.data.models.ProductProblemsData
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class OrderProblemsPresenter @Inject constructor(private val userRepository: UserRepository) : MvpPresenter<OrderProblemsView>() {


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