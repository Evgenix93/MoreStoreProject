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
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class OrderProblemsPhotoFragment
    : MvpAppCompatFragment(R.layout.fragment_problem_goods_photos), OrderProblemsPhotosView {

    private val presenter by moxyPresenter { OrderProblemsPhotoPresenter(requireContext()) }
    private val binding: FragmentProblemGoodsPhotosBinding by viewBinding()
    private val args: OrderProblemsPhotoFragmentArgs by navArgs()

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK || it.data == null) {
                return@registerForActivityResult
            }
            presenter.addPhoto(it.data!!.data!!)
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
            pickPhoto()
        }
        binding.problemGoodsPhotosAddSecondCard.setOnClickListener() {
            pickPhoto()
        }
        binding.problemGoodsPhotosAddThirdCard.setOnClickListener() {
            pickPhoto()
        }
        binding.problemGoodsPhotosAddForthCard.setOnClickListener() {
            pickPhoto()
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

    override fun showPhoto(data: Uri, position: Int) {
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