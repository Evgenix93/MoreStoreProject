package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.databinding.FragmentAddressUserdataBinding
import com.project.morestore.presentation.dialogs.AddressDeleteDialog
import com.project.morestore.data.models.MyAddress
import com.project.morestore.presentation.mvpviews.MyAddressPickupView
import com.project.morestore.domain.presenters.CreateMyAddressPickupPresenter
import com.project.morestore.domain.presenters.EditMyAddressPickupPresenter
import com.project.morestore.util.*
import com.project.morestore.presentation.widgets.loading.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class MyAddressPickupFragment : MvpAppCompatFragment(), MyAddressPickupView {
    companion object {
        const val ADDRESS = "address"
        const val EDIT_ADDRESS = "edit_address"
    }

    private val views by lazy { FragmentAddressUserdataBinding.inflate(layoutInflater) }

    @Inject
    lateinit var editMyAddressPickupPresenter: EditMyAddressPickupPresenter
    @Inject
    lateinit var createMyAddressPickupPresenter: CreateMyAddressPickupPresenter
    private val presenter by moxyPresenter {
        if (isEdit) {
            editMyAddressPickupPresenter
        } else {
            createMyAddressPickupPresenter
        }
    }
    private val isEdit by lazy { args.getParcelable<MyAddress>(EDIT_ADDRESS) != null }
    private var waiting: LoadingDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = views.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(views.toolbar) {
            if (isEdit) {
                val address = args.getParcelable<MyAddress>(EDIT_ADDRESS)!!.address
                title.text = "${address.city} ${address.street} ${address.house}"
                views.delete.visibility = VISIBLE
                views.delete.setOnClickListener { (presenter as EditMyAddressPickupPresenter).delete() }
            } else {
                title.setText(R.string.myAddress_new_title)
            }
            root.attachNavigation()
        }
        views.save.setOnClickListener {
            presenter.save(
                views.fullname.string,
                views.phoneNumber.string,
                requireArguments().getParcelable(EDIT_ADDRESS)!!,
                requireArguments().getParcelable(ADDRESS)!!
            )
        }
        views.phoneNumber.setPhoneField()
        views.phoneNumber.setText("+7 (")
        views.defaultAddress.setOnCheckedChangeListener { _, check -> presenter.changeDefault(check) }
        presenter.setInfo(requireArguments().getParcelable(EDIT_ADDRESS)!!)
    }

    override fun showFullname(fullname: String) = views.fullname.setText(fullname)

    override fun showPhone(phoneNumber: String) = views.phoneNumber.setText(phoneNumber)

    override fun showIsDefault(isDefault: Boolean) {
        views.defaultAddress.apply {
            isChecked = isDefault
            jumpDrawablesToCurrentState()
        }
    }

    override fun showMessage(message: String) = defToast(message)

    override fun showLoading(show: Boolean) {
        if (show) {
            waiting = LoadingDialog(requireContext()).also { it.show() }
        } else {
            waiting?.dismiss()
            waiting = null
        }
    }

    override fun showConfirmDelete() {
        AddressDeleteDialog(requireContext()) {
            (presenter as EditMyAddressPickupPresenter).confirmDelete(requireArguments().getParcelable(EDIT_ADDRESS)!!)
        }.show()
    }

    override fun back() {
        findNavController().popBackStack()
    }

}