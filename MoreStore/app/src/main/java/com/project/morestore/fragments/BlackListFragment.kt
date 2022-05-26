package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.UsersAdapter
import com.project.morestore.databinding.FragmentWaitReviewBinding
import com.project.morestore.dialogs.DeleteDialog
import com.project.morestore.models.User
import com.project.morestore.mvpviews.BlackListMvpView
import com.project.morestore.presenters.BlackListPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class BlackListFragment: MvpAppCompatFragment(R.layout.fragment_wait_review), BlackListMvpView {
    private val presenter by  moxyPresenter { BlackListPresenter(requireContext()) }
    private val binding: FragmentWaitReviewBinding by viewBinding()
    private var usersAdapter: UsersAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initUsersRecyclerView()
        getBlackList()
    }

    private fun getBlackList(){
        presenter.getBlackList()
    }

    private fun initToolbar(){
        binding.toolbar.apply { titleTextView.text = "Заблокированные"
            actionTextView.isVisible = false
            arrowBackImageView.setOnClickListener{findNavController().popBackStack()}
        }
    }

    private fun initUsersRecyclerView(){
        with(binding.sellersRecyclerView){
            adapter = UsersAdapter{
             DeleteDialog(
                context =  requireContext(),
                title = "Разблокировать пользователя?",
                 message = null,
                 confirmText = "Да",
                 cancelText = "Нет",
                 confirmCallback = {presenter.blockUnblockUser()}
             ).show()
            }.also{usersAdapter = it}
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun emptyList(isEmpty: Boolean){
        binding.emptyListImageView.isVisible = isEmpty
        binding.emptyListTextView.isVisible = isEmpty
        binding.sellersRecyclerView.isVisible = isEmpty.not()
    }

    override fun onBlackListLoaded(users: List<User>) {
        usersAdapter.updateList(users)
    }

    override fun onBlockUnblockUser() {
        getBlackList()
    }

    override fun loading(isLoading: Boolean) {
        binding.loader.isVisible = isLoading
    }

    override fun onEmptyList(isEmpty: Boolean) {
        emptyList(isEmpty)
    }

    override fun onError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}