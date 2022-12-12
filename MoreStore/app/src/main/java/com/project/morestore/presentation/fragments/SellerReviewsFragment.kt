package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.presentation.adapters.ReviewsAdapter
import com.project.morestore.presentation.fragments.MediaFragment.Companion.PHOTOS
import com.project.morestore.data.models.ReviewListItem
import com.project.morestore.presentation.mvpviews.ReviewView
import com.project.morestore.domain.presenters.ReviewsPresenter
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class SellerReviewsFragment(): MvpAppCompatFragment(), ReviewView {
    companion object{
        const val USER_ID = "id"
    }
    constructor(userId :Long) :this(){
        arguments = bundleOf(USER_ID to userId)
    }
    private lateinit var list :RecyclerView
    @Inject lateinit var _presenter: ReviewsPresenter
    private val presenter by moxyPresenter { _presenter }
    private val adapter = ReviewsAdapter({
        findNavController().navigate(
            R.id.feedbackProductFragment,
            bundleOf(FeedbackProductFragment.USER_ID to requireArguments().getLong(USER_ID))
        )
    }, {
        val photosArray = it.photo?.map { it.photo }?.toTypedArray()!!
        findNavController().navigate(
            R.id.mediaFragment, bundleOf(PHOTOS to photosArray )
        )
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.page_reviews, container, false)!!
        .also { list = it as RecyclerView }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.initReviews(arguments?.getLong(USER_ID))
        list.adapter = adapter
        list.setSpace(8.dp)
    }

    //IMPLEMENTATION
    override fun showReviews(reviewItems: List<ReviewListItem>) {
        adapter.setItems(reviewItems)
    }

    override fun showReviewButton(show: Boolean) {
        adapter.showReviewButton(show)

    }
}