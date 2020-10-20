package com.oskgro.passbeat.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.oskgro.passbeat.R
import com.oskgro.passbeat.databinding.FragmentLoginBinding
import com.oskgro.passbeat.model.RhythmEncoder
import com.oskgro.passbeat.util.EventObserver
import com.oskgro.passbeat.util.InjectorUtils
import com.oskgro.passbeat.viewModel.LoginViewModel
import kotlin.math.sqrt

class LoginFragment: Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var viewModel: LoginViewModel
    val contentView = R.layout.fragment_login

    var encodingState: Int = 0 // 0 = not started, 1 = started listening
    var timestampMillis: Long = 0
    var rhythmEncoder: RhythmEncoder = RhythmEncoder()
    var stillListeningToTap: Boolean = true

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
        super.onViewCreated(view, savedInstanceState)
        //InjectorUtils.injectSharedPreferences(requireContext()).clearSharedPreferences()
        setClickListeners()
        setupObservers()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListeners() {
        binding.yellowButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(event?.action == MotionEvent.ACTION_DOWN) {
                    animateBigger(v)
                    if(encodingState == 0) {
                        encodingState = 1
                        timestampMillis = System.currentTimeMillis()
                    }
                    else if(encodingState == 1) {
                        val deltaTime = System.currentTimeMillis() - timestampMillis
                        rhythmEncoder.addSegment(deltaTime)
                        timestampMillis = System.currentTimeMillis()
                    }
                    return true
                }
                else if(event?.action == MotionEvent.ACTION_UP) {
                    animateToNormal(v)

                    val deltaTime = System.currentTimeMillis() - timestampMillis
                    rhythmEncoder.addSegment(deltaTime)
                    timestampMillis = System.currentTimeMillis()

                    Handler(Looper.getMainLooper()).postDelayed({
                        if((timestampMillis + 2000) < System.currentTimeMillis()){
                            encodingState = 0
                            Log.i("TEST!!!!",""+rhythmEncoder.display())
                            rhythmEncoder.clear()
                        }
                    }, 2010)

                    return true
                }
                return false
            }
        })
    }

    private fun animateBigger(v: View?) {
        v?.animate()!!
            .scaleY(1.1f)
            .scaleX(1.1f)
            .setDuration(50L)
            .start()
    }

    private fun animateToNormal(v: View?) {
        v?.animate()!!
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(50L)
            .start()
    }

    private fun setupObservers() {
        viewModel.navigateToResultFragment.observe(viewLifecycleOwner, EventObserver {
            if(findNavController().currentDestination?.id == R.id.loginFragment) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToResultFragment())
            }
        })

        // observer for button to navigation to setup fragment
        viewModel.navigateToSetupFragment.observe(viewLifecycleOwner, EventObserver {
            val str = InjectorUtils.injectSharedPreferences(requireContext()).loadRhythmEncoding().display()
            Log.i("LOAD: ","testing: "+str)
            if(str != "") Toast.makeText(context, "Rhythm already set up", Toast.LENGTH_SHORT).show()
            else{
                if(findNavController().currentDestination?.id == R.id.loginFragment) {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSetupFragment())
                }
            }
        })

        // observer for button to navigation to about fragment
        viewModel.navigateToAboutFragment.observe(viewLifecycleOwner, EventObserver {
            if(findNavController().currentDestination?.id == R.id.loginFragment) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAboutFragment())
            }
        })
    }

    // returns true if password is correct
    private fun evaluateRhythm(userInput: RhythmEncoder, password: RhythmEncoder, threshold: Int): Boolean{
        // get rhythm codes to same length
        val lenInput = userInput.getRhythmCode().size
        val lenPswd = password.getRhythmCode().size

        if(lenInput != lenPswd){
            return false
        }

        //calculate weighted mean squared error (= distance between vectors)
        var diffTap: ArrayList<Long> = ArrayList()
        var diffPause: ArrayList<Long> = ArrayList()
        for (i in 0 until lenInput){
            var temp = userInput.getRhythmCode().get(i) - password.getRhythmCode().get(i)
            temp = temp * temp
            if(i%2 == 0){
                // tap
                diffTap.add(temp)
            } else{
                //pause
                diffPause.add(temp)
            }
        }
        val errorTap = sqrt(diffTap.sum().toDouble())
        val errorPause = sqrt(diffPause.sum().toDouble())
        val weight = 0.55 // TODO change!
        val error =(errorTap*weight + errorPause*(1-weight)).toInt()
        Log.i("TEST!!!!","error: "+error)
    }
}
