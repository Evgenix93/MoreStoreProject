package com.project.morestore.fragments

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.MainFragmenViewPagerAdapter
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.adapters.SuggestionArrayAdapter
import com.project.morestore.databinding.FragmentMainBinding
import com.project.morestore.models.*
import com.project.morestore.models.Filter
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class MainFragment : MvpAppCompatFragment(R.layout.fragment_main), MainMvpView {
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private val binding: FragmentMainBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()
    private var kidsProductAdapter: ProductAdapter by autoCleared()
    private var isMainLoaded = false
    private var currentSuggestionModels: SuggestionModels? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isMainLoaded = false
        getUserData()
        showBottomNavBar()
        initToolbar()
        initLists()
        initViewPager()
        setClickListeners()
        initPermissionLauncher()
        getCurrentUserAddress()

        binding.toolbarMain.materialToolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.favorite -> {
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToFavoritesFragment())
                    true
                }
                R.id.cart -> {
                    findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToOrdersCartFragment()
                    )
                    true
                }
                else -> true
            }
        }





    }

    private fun showUnreadMessagesStatus(){
        presenter.showUnreadMessages()
        //getUserData()

    }

    private fun bindFilter(filter: Filter) {
        if (filter.chosenForWho[0]) {
            binding.forWomenTextView.text = "Женщинам"
            presenter.getProducts(forWho = listOf(true, false, false), isFiltered = true)
        }
        if (filter.chosenForWho[1]) {
            binding.forWomenTextView.text = "Мужчинам"
            presenter.getProducts(forWho = listOf(false, true, false), isFiltered = true)
        }
        if (filter.chosenForWho[2]) {
            presenter.getProducts(forWho = listOf(false, false, true), isFiltered = true)
        }
    }


    private fun initLists() {
        productAdapter =
            ProductAdapter(null) {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToProductDetailsFragment(
                        it,
                        null,
                        false
                    )
                )
            }

        kidsProductAdapter =
            ProductAdapter(null) {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToProductDetailsFragment(
                        it,
                        null,
                        false
                    )
                )
            }


        with(binding.forWomenRecyclerView) {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            isNestedScrollingEnabled = false
        }

        with(binding.forKidsRecyclerView) {
            adapter = kidsProductAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            isNestedScrollingEnabled = false
        }




    }

    private fun initViewPager() {
        binding.viewPager2.adapter = MainFragmenViewPagerAdapter(this)
        binding.dots.setViewPager2(binding.viewPager2)
    }

    private fun showBottomNavBar() {
        val mainActivity = activity as MainActivity
        mainActivity.showBottomNavBar(true)
    }

    private fun initToolbar() {
        val toolbar = binding.toolbarMain.materialToolbar

        val searchItem = toolbar.menu.findItem(R.id.search)
        searchItem.setOnMenuItemClickListener {
            binding.toolbarMain.searchFrameLayout.isVisible = true
            it.isVisible = false
            toolbar.menu.findItem(R.id.favorite).isVisible = false
            toolbar.menu.findItem(R.id.cart).isVisible = false
            toolbar.logo = null
            binding.toolbarMain.backImageView.isVisible = true
            true
        }
        binding.toolbarMain.clearImageView.setOnClickListener {
            binding.toolbarMain.searchEditText.text.clear()
        }

        binding.toolbarMain.backImageView.setOnClickListener {
            binding.toolbarMain.backImageView.isVisible = false
            binding.toolbarMain.searchFrameLayout.isVisible = false
            searchItem.isVisible = true
            toolbar.menu.findItem(R.id.favorite).isVisible = true
            toolbar.menu.findItem(R.id.cart).isVisible = true
            toolbar.logo =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_logo_more_store, null)
        }


        val searchEditText = binding.toolbarMain.searchEditText
        searchEditText.dropDownAnchor = R.id.anchor

        val searchFlow = callbackFlow<String> {
            val searchListener = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    sendBlocking(newText.toString())
                    currentSuggestionModels = null

                }

                override fun afterTextChanged(p0: Editable?) {
                    binding.searchBtn.isVisible = !p0.isNullOrEmpty()
                }
            }
            searchEditText.addTextChangedListener(searchListener)
            awaitClose { searchEditText.removeTextChangedListener(searchListener) }
        }

        presenter.collectSearchFlow(searchFlow)
    }

    private fun setClickListeners() {
        binding.materialCardView10.setOnClickListener {
            presenter.updateBrand(
                ProductBrand(
                    1232,
                    "ZARA",
                    3,
                    true,
                    false
                )
            )
        }
        binding.materialCardView11.setOnClickListener {
            presenter.updateBrand(
                ProductBrand(
                    1045,
                    "STRADIVARIUS",
                    3,
                    true,
                    false
                )
            )
        }
        binding.materialCardView12.setOnClickListener {
            presenter.updateBrand(
                ProductBrand(
                    468,
                    "H&M",
                    3,
                    true,
                    false
                )
            )
        }
        binding.materialCardView13.setOnClickListener {
            presenter.updateBrand(
                ProductBrand(
                    685,
                    "MANGO",
                    3,
                    true,
                    false
                )
            )
        }
        binding.materialCardView14.setOnClickListener {
            presenter.updateBrand(
                ProductBrand(
                    625,
                    "LEVI'S",
                    2,
                    true,
                    false
                )
            )
        }
        binding.materialCardView15.setOnClickListener {
            presenter.updateBrand(
                ProductBrand(
                    588,
                    "LACOSTE",
                    2,
                    true,
                    false
                )
            )
        }




        binding.moreBrandsTextView.setOnClickListener { findNavController().navigate(R.id.brandsFragment) }
        binding.offersTextView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCatalogFragment(
                    forWho = if (binding.forWomenTextView.text == "Женщинам") "women" else "men"
                )
            )
        }

        binding.moreForKidsTextView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCatalogFragment(forWho = "kids")
            )
        }
        binding.searchBtn.setOnClickListener {
            val queryStr = binding.toolbarMain.searchEditText.text.toString()
            Log.d("mylog", "query: $queryStr")
            if (currentSuggestionModels != null)
                presenter.getSuggestionProducts(currentSuggestionModels!!)
            else
                findNavController().navigate(
                    R.id.catalogFragment,
                    Bundle().apply { putString("query", queryStr) })
        }
        binding.allProductsCardView.setOnClickListener {
            presenter.updateProductCategories(listOf())
        }
        binding.bagsCardView.setOnClickListener {
            presenter.updateProductCategories(
                listOf(
                    ProductCategory(
                        20,
                        "Сумки и Аксессуары",
                        true
                    )
                )
            )
        }
        binding.shoesCardView.setOnClickListener {
            presenter.updateProductCategories(listOf(ProductCategory(5, "Обувь", true)))
        }
        binding.clothesCardView.setOnClickListener {
            presenter.updateProductCategories(
                listOf(
                    ProductCategory(3, "Бельё", true),
                    ProductCategory(4, "Брюки", true),
                    ProductCategory(6, "Платья и Сарафаны", true),
                    ProductCategory(7, "Юбки", true),
                    ProductCategory(9, "Джинсы", true),
                    ProductCategory(11, "Шорты", true),
                    ProductCategory(12, "Топы и майки", true),
                    ProductCategory(14, "Домашняя одежда", true),
                    ProductCategory(15, "Джемперы и Свитеры", true),
                    ProductCategory(17, "Пиджаки и костюмы", true),
                    ProductCategory(18, "Блузки", true),
                    ProductCategory(10, "Одежда для беременных", true),
                    ProductCategory(13, "Одежда больших размеров", true)
                )
            )
        }

        binding.outerwearCardView.setOnClickListener {
            presenter.updateProductCategories(listOf(ProductCategory(2, "Верхняя одежда", true)))
        }
        binding.accessoriesCardView.setOnClickListener {
            presenter.updateProductCategories(
                listOf(
                    ProductCategory(
                        20,
                        "Сумки и Аксессуары",
                        true
                    )
                )
            )
        }
        binding.sportTourismCardView.setOnClickListener {
            presenter.updateProductCategories(
                listOf(
                    ProductCategory(
                        16,
                        "Спортивная одежда",
                        true
                    )
                )
            )

        }
        binding.kidsCardView.setOnClickListener {
            presenter.updateProductCategories(
                listOf(
                    ProductCategory(21, "Школьная форма", true),
                    ProductCategory(22, "Праздничные костюмы", true)
                )
            )
        }
    }

    private fun loadFilter() {
        presenter.loadFilter()
    }

    private fun getUserData() {
        presenter.getUserData()
    }


    private fun loadOnboardingData() {
        presenter.loadOnboardingData()
    }

    private fun getCity(){
        try {
            val locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
            val cancelToken = object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                    return this

                }

                override fun isCancellationRequested(): Boolean {
                    return false

                }
            }
            locationProvider.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, cancelToken ).addOnCompleteListener { task ->
                if(task.isSuccessful){
                   // Log.d("mylog", task.result.longitude.toString())
                    task.result?.let {
                        presenter.getCityByCoordinates("${it.latitude},${it.longitude}")
                    }
                    task.result ?: run {


                    }
                }else{



                }

            }

        }catch (e: SecurityException){

        }
    }

    private fun initPermissionLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
            if(granted)
                getCity()

        }
    }

    private fun requestPermissions(){
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun getCurrentUserAddress(){
        presenter.getCurrentUserAddress()
    }


    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loaded(result: Any) {
        if (result is List<*>) {
            if (!isMainLoaded) {
                productAdapter.updateList(result as List<Product>)
                isMainLoaded = true
                presenter.getProducts(isFiltered = true, forWho = listOf(false, false, true))
                return
            }

            if (currentSuggestionModels != null) {
                val product = currentSuggestionModels?.list?.find { it.product == true }
                findNavController().navigate(
                    R.id.catalogFragment,
                    bundleOf("query" to product?.text.orEmpty())
                )
                return

            }

            kidsProductAdapter.updateList(result as List<Product>)
        }

        if (result is Filter) {
            bindFilter(result)
        }

        if (result is Unit)
            loadFilter()

        //if(result is User)
          //  if(result.phone == null)
        //        findNavController().navigate(MainFragmentDirections.actionMainFragmentToRegistration3Fragment(phoneOrEmail = result.email.orEmpty(), userId = result.id.toInt(), fromMainFragment = true))

        if(result is Address){
            if(result.fullAddress.isNotEmpty())
                presenter.changeCurrentUserAddress(result)
            else
                requestPermissions()

        }
        if(result is User) {
            if (result.phone == null)
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToRegistration3Fragment(
                        phoneOrEmail = result.email.orEmpty(),
                        userId = result.id.toInt(),
                        fromMainFragment = true
                    )
                )
                    loadOnboardingData()
            showUnreadMessagesStatus()
            //showOnBoarding()
        }
        if(result is Boolean){
            (activity as MainActivity).showUnreadMessagesIcon(result)
        }
    }

    override fun loading() {

    }

    override fun showOnBoarding() {
        Log.d("MyDebug", "showOnBoarding mainFragment")
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToOnboarding1Fragment())
    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {
        binding.toolbarMain.searchEditText.setAdapter(
            SuggestionArrayAdapter(
                requireContext(),
                R.layout.item_suggestion_textview,
                list
            ) { position, string ->
                Log.d("mylog", "click position $position")
                binding.toolbarMain.searchEditText.dismissDropDown()
                presenter.cancelSearchJob()
                binding.toolbarMain.searchEditText.setAdapter(null)
                binding.toolbarMain.searchEditText.setText(string)
                initToolbar()
                currentSuggestionModels = objectList[position]

            }
        )
        binding.toolbarMain.searchEditText.showDropDown()
    }

    override fun success() {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToCatalogFragment())
    }

}