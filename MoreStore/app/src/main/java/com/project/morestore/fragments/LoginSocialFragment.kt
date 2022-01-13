package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentSocialLoginBinding
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.models.User
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.presenters.AuthPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class LoginSocialFragment : MvpAppCompatFragment(R.layout.fragment_social_login), AuthMvpView {
    private val binding: FragmentSocialLoginBinding by viewBinding()
    private val args: LoginSocialFragmentArgs by navArgs()
    private val presenter by moxyPresenter { AuthPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getSocialLoginUrl(args.socialType)

    }

    override fun success(result: Any) {
        if (result is RegistrationResponse) {
            presenter.getUserData()
            return
        }
        if (result is User) {
            if(result.name != null && result.phone != null && result.surname != null){
                presenter.loadOnBoardingViewed()
                //findNavController().navigate(LoginSocialFragmentDirections.actionLoginSocialFragmentToMainFragment())
                return
            }
            findNavController().navigate(
                LoginSocialFragmentDirections.actionLoginSocialFragmentToRegistration3Fragment(
                    code = 0,
                    phoneOrEmail = result.email ?: result.phone.orEmpty(),
                    userId = result.id
                )
            )
            return
        }
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return if (request?.url.toString().contains("?code")) {
                    presenter.loginSocial(request?.url.toString())
                    true
                } else {
                    false
                }
            }

        }
        binding.webView.loadUrl(result as String)

    }

    override fun error(message: String) {
        if(message == "401"){
            findNavController().navigate(LoginSocialFragmentDirections.actionLoginSocialFragmentToMainFragment())
            return
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {

    }

    override fun showOnBoarding() {
        findNavController().navigate(LoginSocialFragmentDirections.actionLoginSocialFragmentToOnboarding1Fragment())

    }

}