package com.oskgro.passbeat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.oskgro.passbeat.R
import com.oskgro.passbeat.databinding.FragmentSetupBinding
import com.oskgro.passbeat.viewModel.SetupViewModel

class SetupFragment: Fragment() {
    lateinit var binding: FragmentSetupBinding
    lateinit var viewModel: SetupViewModel
    val contentView = R.layout.fragment_setup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, contentView, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(SetupViewModel::class.java)
        //binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}