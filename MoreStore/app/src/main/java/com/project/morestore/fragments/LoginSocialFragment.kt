package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
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

    override fun success(result: Any, extra: Any?) {
        if (result is RegistrationResponse) {
            presenter.getUserData()
            return
        }
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return if (request?.url.toString().contains("?code") || request?.url.toString().contains("?state")) {
                    presenter.loginSocial(request?.url.toString())
                    true
                } else {
                    false
                }
            }

        }
        binding.webView.settings.userAgentString = "Chrome/56.0.0.0 Mobile"
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(result as String)

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {

    }



    override fun successNewCode(result: Any) {

    }

    override fun registrationComplete(complete: Boolean, user: User) {
        if(complete) {
            findNavController().navigate(LoginSocialFragmentDirections.actionLoginSocialFragmentToMainFragment())
        }else{
            findNavController().navigate(
                    LoginSocialFragmentDirections.actionLoginSocialFragmentToRegistration3Fragment(
                        code = 0,
                        phoneOrEmail = user.email ?: user.phone.orEmpty(),
                        userId = user.id.toInt()
                    )
                )
        }
    }

}