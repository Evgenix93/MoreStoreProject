package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentSuccessPromoteBinding
import com.project.morestore.presentation.mvpviews.SuccessPromoteView
import com.project.morestore.domain.presenters.SuccessPromotePresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class SuccessPromoteFragment: MvpAppCompatFragment(R.layout.fragment_success_promote), SuccessPromoteView {
    private val binding: FragmentSuccessPromoteBinding by viewBinding()
    private val args: SuccessPromoteFragmentArgs by navArgs()
    @Inject
    lateinit var successPromotePresenter: SuccessPromotePresenter
    private val presenter by moxyPresenter { successPromotePresenter }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.getPromoEndDate(args.tariff)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        setClickListeners()
    }


    private fun initToolbar(){
        with(binding.toolbar){
            titleTextView.text = "Получите больше просмотров"
            backIcon.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setClickListeners(){
        binding.finishBtn.setOnClickListener {
            findNavController().popBackStack(R.id.productDetailsFragment, false)
        }
    }

    override fun showPromoEndDate(date: String) {
        binding.timePeriodInfoTextView.text = "Теперь он окажется в верхних строчках каталога.\nДанная услуга закончится $date"
    }
}