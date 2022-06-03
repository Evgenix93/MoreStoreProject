package com.project.morestore.fragments.orders.cart;

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.SliderMenuAdapter
import com.project.morestore.adapters.cart.CartAdapter
import com.project.morestore.databinding.FragmentOrdersCartBinding
import com.project.morestore.dialogs.DeleteDialog
import com.project.morestore.fragments.CabinetGuestFragment
import com.project.morestore.models.Product
import com.project.morestore.models.SuggestionModels
import com.project.morestore.models.User
import com.project.morestore.models.slidermenu.OrdersSliderMenu
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.presenters.toolbar.cart.ToolbarCartPresenter
import com.project.morestore.presenters.toolbar.cart.ToolbarCartView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class OrdersCartFragment
    : MvpAppCompatFragment(R.layout.fragment_orders_cart), OrdersCartView, ToolbarCartView,
    MainMvpView {

    private val presenter by moxyPresenter { OrdersCartPresenter(requireContext()) }
    private val mainPresenter by moxyPresenter { MainPresenter(requireContext()) }

    private val toolbarPresenter by moxyPresenter {
        ToolbarCartPresenter(requireContext(), OrdersSliderMenu.CART)
    }

    private val binding: FragmentOrdersCartBinding by viewBinding()

    private var userId: Long = 0

    ///////////////////////////////////////////////////////////////////////////
    //                      view
    ///////////////////////////////////////////////////////////////////////////


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenCheck()
        showBottomNav()
        initToolbar()
        mainPresenter.getUserId()
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      toolbar cart view
    ///////////////////////////////////////////////////////////////////////////

    override fun navigate(pageId: Int?) {
        val navOptions =  NavOptions.Builder().setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false).build()
        when (pageId) {
            null -> {
                findNavController().popBackStack()
            }
            R.id.ordersActiveFragment -> {
                findNavController().navigate(OrdersCartFragmentDirections.actionOrdersCartFragmentToOrdersActiveFragment(),
                navOptions)
            }
            R.id.ordersHistoryFragment -> {
                findNavController().navigate(OrdersCartFragmentDirections.actionOrdersCartFragmentToOrdersHistoryFragment(), navOptions)
            }
            R.id.salesActiveFragment ->{
                findNavController().navigate(OrdersCartFragmentDirections.actionOrdersCartFragmentToSalesActiveFragment(), navOptions)
            }
            R.id.salesHistoryFragment -> {
                findNavController().navigate(R.id.salesHistoryFragment, null, navOptions)
            }

            R.id.cabinetGuestFragment -> findNavController().navigate(R.id.cabinetGuestFragment, bundleOf(CabinetGuestFragment.FRAGMENT_ID to R.id.cabinetGuestFragment), navOptions)
        }
    }

    override fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>) {
        binding.toolbar.sliderMenu.adapter = adapter
        binding.toolbar.sliderMenu.scrollToPosition(0)
    }

    override fun navigate(product: Product, cartId: Long) {
        findNavController().navigate(
            OrdersCartFragmentDirections.actionOrdersCartFragmentToCreateOrderFragment(
                product,
                cartId
            )
        )
    }

    override fun navigate(user: User) {
        findNavController().navigate(OrdersCartFragmentDirections.actionOrdersCartFragmentToSellerProfileFragment(user = user, toReviews = false))
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      Orders cart view
    ///////////////////////////////////////////////////////////////////////////

    override fun initCart(adapter: CartAdapter) {
        showLoading(false)
        binding.recyclerView.adapter = adapter
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      Main View
    ///////////////////////////////////////////////////////////////////////////

    override fun loaded(result: Any) {
        showLoading(false)
        when (result) {
            is Long -> {
                userId = result
                Log.d("MyDebug", "userId = $result")
                showLoading(true)
                presenter.loadCartData(result) {
                    mainPresenter.removeProductFromCart(it.product.id, userId)
                }
            }
            is Boolean -> {
                if(result)
                navigate(R.id.cabinetGuestFragment)
            }
        }
    }

    override fun loading() {
        showLoading(true)
    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showDeleteDialog(deleteDialog: DeleteDialog) {
        if (isAdded) {
            deleteDialog.show()
        }
    }

    override fun showOnBoarding() {
    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {
    }

    override fun loginFailed() {
        TODO("Not yet implemented")
    }

    override fun success() {
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    private fun showBottomNav() {
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun initToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            toolbarPresenter.onBackClick()
        }
        binding.toolbar.toolbarLike.setOnClickListener{
            findNavController().navigate(R.id.favoritesFragment)
        }
    }

    private fun tokenCheck(){
        mainPresenter.tokenCheck()
    }

    private fun showLoading(isLoading: Boolean){
        binding.loader.isVisible = isLoading
    }
}