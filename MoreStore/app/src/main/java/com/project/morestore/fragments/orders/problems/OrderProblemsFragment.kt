package com.project.morestore.fragments.orders.problems;

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentProblemGoodsBinding
import com.project.morestore.dialogs.ProblemTypeDialog
import com.project.morestore.data.models.ProductProblemsData
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.MaskedTextChangedListener.Companion.installOn
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject


@AndroidEntryPoint
class OrderProblemsFragment
    : MvpAppCompatFragment(R.layout.fragment_problem_goods), OrderProblemsView {

    @Inject
    lateinit var orderProblemsPresenter: OrderProblemsPresenter
    private val presenter by moxyPresenter { orderProblemsPresenter }
    private val binding: FragmentProblemGoodsBinding by viewBinding()
    private val args: OrderProblemsFragmentArgs by navArgs()
    private lateinit var listener: MaskedTextChangedListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        showBottomNav()
        initToolbar()
        initContent()
    }

    ///////////////////////////////////////////////////////////////////////////
    //                          OrderProblemsView
    ///////////////////////////////////////////////////////////////////////////

    override fun openProblemsPhotosPage(productProblemsData: ProductProblemsData) {
        findNavController().navigate(OrderProblemsFragmentDirections.actionOrderProblemsFragmentToOrderProblemsPhotoFragment(productProblemsData))
    }

    override fun setProductData(productProblemsData: ProductProblemsData) {

        var hasProblem = false;
        if (productProblemsData.problem != null) {
            binding.problemGoodsReasonContent.text = productProblemsData.problem
            hasProblem = true
        }

        var hasComment = false
        if (productProblemsData.comment != null) {
            val length = productProblemsData.comment?.length ?: 0
            binding.problemGoodsCommentsLength.text = length.toString()
            hasComment = length in 1..3000
        }

        val hasAllData = hasProblem && hasComment && productProblemsData.phone != null
        binding.problemGoodsNext.isEnabled = hasAllData
    }

    override fun showReasonDialog(problemDialog: ProblemTypeDialog) {
        if (isAdded) {
            problemDialog.show(parentFragmentManager, ProblemTypeDialog.TAG)
        }
    }

    override fun setPhone(phone: String) {
        listener.setText(phone)
    }

    ///////////////////////////////////////////////////////////////////////////
    //                          Private
    ///////////////////////////////////////////////////////////////////////////

    private fun setClickListeners() {
        binding.problemGoodsNext.setOnClickListener() {
            presenter.onNextClick(args.productId);
        }
        binding.problemGoodsReasonContainer.setOnClickListener {
            presenter.reasonOnClick();
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
        binding.problemGoodsCommentContent.addTextChangedListener {
            presenter.onCommentChange(it.toString())
        }

        listener = installOn(
            binding.problemGoodsPhone,
            "+7([000])-[000]-[00]-[00]",
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String
                ) {
                    if (maskFilled) {
                        presenter.onPhoneChanged(extractedValue)
                    } else {
                        presenter.onPhoneChanged(null);
                    }
                }
            }
        )


        binding.problemGoodsPhone.hint = listener.placeholder()
    }
}