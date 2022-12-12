package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.presentation.adapters.FeedbackProductsAdapter
import com.project.morestore.databinding.FragmentFeedbackProductsBinding
import com.project.morestore.presentation.fragments.base.FullscreenFragment
import com.project.morestore.data.models.FeedbackProduct
import com.project.morestore.presentation.mvpviews.FeedbackProductView
import com.project.morestore.domain.presenters.FeedbackProductPresenter
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FeedbackProductFragment() :FullscreenFragment(), FeedbackProductView {
    companion object{
        const val USER_ID = "user_id"
        const val PRODUCT_ID = "product_id"
    }

    private val adapter = FeedbackProductsAdapter{
        findNavController()
            .navigate(R.id.ratingFragment, bundleOf(PRODUCT_ID to it.id))
    }
    private lateinit var views :FragmentFeedbackProductsBinding

    @Inject lateinit var _presenter: FeedbackProductPresenter
    private val presenter by moxyPresenter { _presenter }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFeedbackProductsBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getProducts(arguments?.getLong(USER_ID))
        with(views){
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
            list.adapter = adapter
            list.setSpace(8.dp)
        }
    }

    //IMPLEMENTATION
    override fun showProducts(products: List<FeedbackProduct>) {
        adapter.setItems(products)
    }
}