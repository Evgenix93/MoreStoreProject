package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentSuccessPromoteBinding
import com.project.morestore.mvpviews.SuccessPromoteView
import com.project.morestore.presenters.SuccessPromotePresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*

class SuccessPromoteFragment: MvpAppCompatFragment(R.layout.fragment_success_promote), SuccessPromoteView {
    private val binding: FragmentSuccessPromoteBinding by viewBinding()
    private val args: SuccessPromoteFragmentArgs by navArgs()
    private val presenter by moxyPresenter { SuccessPromotePresenter() }

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