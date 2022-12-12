package com.project.morestore.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentAddCardBinding
import com.project.morestore.presentation.mvpviews.AddCardMvpView
import com.project.morestore.domain.presenters.AddCardPresenter
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class AddCardFragment: MvpAppCompatFragment(R.layout.fragment_add_card), AddCardMvpView {
    private val binding: FragmentAddCardBinding by viewBinding()
    private lateinit var scanLauncher: ActivityResultLauncher<Intent>
    @Inject lateinit var addCardPresenter: AddCardPresenter
    private val presenter by moxyPresenter { addCardPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditTexts()
        initToolbar()
        initLauncher()
        setClickListeners()
    }


    private fun initEditTexts(){
        val cardNumberListener = MaskedTextChangedListener("[0000]-[0000]-[0000]-[0000]", binding.editText)
        binding.editText.addTextChangedListener(cardNumberListener)
        val cardDateListener = MaskedTextChangedListener("[00]/[00]", binding.editText4)
        binding.editText4.addTextChangedListener(cardDateListener)
        val cardCvvListener = MaskedTextChangedListener("[000]", binding.editText2)
        binding.editText2.addTextChangedListener(cardCvvListener)
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.isVisible = false
    }

    private fun setClickListeners(){
        binding.payButton.setOnClickListener {
            presenter.addCard(binding.editText.text.toString())
        }
        binding.scanCardBtn.setOnClickListener {
            scanCard()
        }
    }

    private fun scanCard(){
        val scanIntent = Intent(requireContext(), CardIOActivity::class.java)

        // customize these values to suit your needs.

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false) // default: false

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false) // default: false

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false) // default: false


        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        //startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE)
        scanLauncher.launch(scanIntent)
    }

    private fun initLauncher(){
         scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
            data.data ?: return@registerForActivityResult
            if ( data!!.data!!.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)){
                val scanResult = data.data!!.getParcelableExtra<CreditCard>(CardIOActivity.EXTRA_SCAN_RESULT)
                binding.editText.setText(scanResult?.cardNumber)
            }


        }
    }



    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    }

    override fun success() {
        binding.loader.isVisible = false
        findNavController().popBackStack()

    }

    override fun loading() {
        binding.loader.isVisible = true
    }


}