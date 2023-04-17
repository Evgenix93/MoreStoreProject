package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.UsersAdapter
import com.project.morestore.databinding.FragmentWaitReviewBinding
import com.project.morestore.presentation.dialogs.DeleteDialog
import com.project.morestore.data.models.User
import com.project.morestore.presentation.mvpviews.BlackListMvpView
import com.project.morestore.domain.presenters.BlackListPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class BlackListFragment: MvpAppCompatFragment(R.layout.fragment_wait_review), BlackListMvpView {
    @Inject lateinit var blackListPresenter: BlackListPresenter
    private val presenter by  moxyPresenter { blackListPresenter }
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
        binding.toolbar.apply { titleTextView.text = getString(R.string.blocked_users)
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
                 confirmCallback = {presenter.blockUnblockUser(it.id)}
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