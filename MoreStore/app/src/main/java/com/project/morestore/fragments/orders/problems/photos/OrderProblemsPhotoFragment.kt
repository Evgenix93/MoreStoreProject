package com.project.morestore.fragments.orders.problems.photos;

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentProblemGoodsPhotosBinding
import com.project.morestore.dialogs.FeedbackCompleteDialog
import com.project.morestore.fragments.orders.problems.OrderProblemsFragmentDirections
import com.project.morestore.fragments.orders.problems.OrderProblemsPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class OrderProblemsPhotoFragment
    : MvpAppCompatFragment(R.layout.fragment_problem_goods_photos), OrderProblemsPhotosView {

    @Inject
    lateinit var orderProblemsPhotoPresenter: OrderProblemsPhotoPresenter
    private val presenter by moxyPresenter { orderProblemsPhotoPresenter }
    private val binding: FragmentProblemGoodsPhotosBinding by viewBinding()
    private val args: OrderProblemsPhotoFragmentArgs by navArgs()

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK || it.data == null) {
                return@registerForActivityResult
            }

        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        showBottomNav()
        initToolbar()
        initContent()
    }

    private fun setClickListeners() {
        binding.problemGoodsPhotosNext.setOnClickListener {
            presenter.onNextClick(args.problemData);
        }

        binding.problemGoodsPhotosAddFirstCard.setOnClickListener() {
            //pickPhoto()
            findNavController().navigate(OrderProblemsPhotoFragmentDirections.actionOrderProblemsPhotoFragmentToMakePhotoFragment(1))
        }
        binding.problemGoodsPhotosAddSecondCard.setOnClickListener() {
            //pickPhoto()
            findNavController().navigate(OrderProblemsPhotoFragmentDirections.actionOrderProblemsPhotoFragmentToMakePhotoFragment(2))


        }
        binding.problemGoodsPhotosAddThirdCard.setOnClickListener() {
            //pickPhoto()
            findNavController().navigate(OrderProblemsPhotoFragmentDirections.actionOrderProblemsPhotoFragmentToMakePhotoFragment(3))

        }
        binding.problemGoodsPhotosAddForthCard.setOnClickListener() {
            //pickPhoto()
            findNavController().navigate(OrderProblemsPhotoFragmentDirections.actionOrderProblemsPhotoFragmentToMakePhotoFragment(4))

        }
    }

    private fun showBottomNav() {
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun initToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initContent() {
        presenter.getPhotos()
    }

    //PRIVATE
    private fun pickPhoto() {
        Intent().apply {
            type = "image/*"
            action = ACTION_GET_CONTENT
        }
            .also { photoPickerLauncher.launch(it) }
    }

    ///////////////////////////////////////////////////////////////////////////
    //                            View
    ///////////////////////////////////////////////////////////////////////////

    override fun showPhoto(data: File, position: Int) {
        val loadTo = getPhotoTarget(position)

        if (loadTo != null) {
            Glide.with(this)
                .load(data)
                .into(loadTo)
        }
    }

    override fun navigateBack() {
        findNavController().navigate(
            OrderProblemsPhotoFragmentDirections
                .actionOrderProblemsPhotoFragmentToOrdersActiveFragment()
        )
    }

    override fun showCompleteDialog(dialog: FeedbackCompleteDialog) {
        if (isAdded){
            dialog.show()
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    fun getPhotoTarget(position: Int) =
        when (position) {
            1 -> binding.problemGoodsPhotosAddFirstPhoto
            2 -> binding.problemGoodsPhotosAddSecondPhoto
            3 -> binding.problemGoodsPhotosAddThirdPhoto
            4 -> binding.problemGoodsPhotosAddForthPhoto
            else -> null
        }
}