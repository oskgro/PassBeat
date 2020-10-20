package com.oskgro.passbeat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.oskgro.passbeat.R
import com.oskgro.passbeat.databinding.FragmentLoginBinding
import com.oskgro.passbeat.util.EventObserver
import com.oskgro.passbeat.viewModel.LoginViewModel

class LoginFragment: Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var viewModel: LoginViewModel
    val contentView = R.layout.fragment_login

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, contentView, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupObservers() {
        viewModel.navigateToResultFragment.observe(viewLifecycleOwner, EventObserver {
            if(findNavController().currentDestination?.id == R.id.loginFragment) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToResultFragment())
            }
        })

        // observer for button to navigation to setup fragment
        viewModel.navigateToSetupFragment.observe(viewLifecycleOwner, EventObserver {
            if(findNavController().currentDestination?.id == R.id.loginFragment) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSetupFragment())
            }
        })

        // observer for button to navigation to about fragment
        viewModel.navigateToAboutFragment.observe(viewLifecycleOwner, EventObserver {
            if(findNavController().currentDestination?.id == R.id.loginFragment) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAboutFragment())
            }
        })
    }

}