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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.oskgro.passbeat.R
import com.oskgro.passbeat.databinding.FragmentLoginBinding
import com.oskgro.passbeat.model.RhythmEncoder
import com.oskgro.passbeat.util.EventObserver
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

    var testPswd: RhythmEncoder = RhythmEncoder() //TODO remove!

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

        setClickListeners()
        setupObservers()

        //TODO remove!
        if(testPswd.getRhythmCode().isEmpty()){
            testPswd.addSegment(85)
        }
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
                            grantAccess(rhythmEncoder, testPswd)
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

    // returns true if password is correct
    private fun evaluateRhythm(userInput: RhythmEncoder, password: RhythmEncoder, threshold: Int): Boolean{
        // get rhythm codes to same length
        val lenInput = userInput.getRhythmCode().size
        val lenPswd = password.getRhythmCode().size

        if(lenInput != lenPswd){
            Log.i("TEST!!!!", "invalid password lenght")
            return false
        }

        //calculate mean squared error (=distance between vectors)
        var diff = userInput.getRhythmCode()
        for (i in 0 until lenInput){
            var temp = diff.get(i)- password.getRhythmCode().get(i)
            diff.set(i,temp*temp)
        }
        val error = sqrt(diff.sum().toDouble()).toInt()
        Log.i("TEST!!!!","error: "+error)

        return error < threshold
    }

    private fun grantAccess(userInput: RhythmEncoder, password: RhythmEncoder){
        var threshold = 150 //TODO change!
        //Log.i("TEST!!!!", "password: "+password.display())
        if (evaluateRhythm(userInput, password, threshold)){
            if(findNavController().currentDestination?.id == R.id.loginFragment) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToResultFragment())
            }
        } else{
            Log.i("TEST!!!!", "access denied")
        }
    }
}