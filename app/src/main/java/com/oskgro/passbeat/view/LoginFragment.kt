package com.oskgro.passbeat.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
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
import kotlin.math.abs
import kotlin.math.sqrt

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var viewModel: LoginViewModel
    val contentView = R.layout.fragment_login

    var encodingState: Int = 0 // 0 = not started, 1 = started listening
    var timestampMillis: Long = 0
    var rhythmEncoder: RhythmEncoder = RhythmEncoder()
    var rhythmIsSetUp: Boolean = false
    lateinit var haptic: Vibrator

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
        //InjectorUtils.injectSharedPreferences(requireContext()).clearSharedPreferences(requireContext())
        setClickListeners()
        setupObservers()
        if(!InjectorUtils.injectSharedPreferences(requireContext()).loadRhythmEncoding().isEmpty()) {
            rhythmIsSetUp = true
        }
        haptic = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListeners() {
        binding.yellowButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    animateBigger(v)
                    val pattern: LongArray = longArrayOf(0, 200, 0)
                    if(!viewModel.appIsMuted.value!!) haptic.vibrate(pattern, 0)
                    if (encodingState == 0) {
                        if(!rhythmIsSetUp) {
                            haptic.cancel()
                            navToSetup()
                        }
                        else {
                            encodingState = 1
                            timestampMillis = System.currentTimeMillis()
                        }
                    } else if (encodingState == 1) {
                        val deltaTime = System.currentTimeMillis() - timestampMillis
                        rhythmEncoder.addSegment(deltaTime)
                        timestampMillis = System.currentTimeMillis()
                    }
                    return true
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    animateToNormal(v)
                    haptic.cancel()
                    val deltaTime = System.currentTimeMillis() - timestampMillis
                    rhythmEncoder.addSegment(deltaTime)
                    timestampMillis = System.currentTimeMillis()

                    Handler(Looper.getMainLooper()).postDelayed({
                        if ((timestampMillis + 2000) < System.currentTimeMillis()) {
                            encodingState = 0
                            Log.i("USER INPUT", "" + rhythmEncoder.display())
                            if (evaluateRhythmPercentage(
                                    rhythmEncoder,
                                    InjectorUtils.injectSharedPreferences(requireContext())
                                        .loadRhythmEncoding(),
                                    0.1
                                )
                            ) {
                                Toast.makeText(context, "Access granted!", Toast.LENGTH_SHORT).show()
                                rhythmEncoder.clear()
                                navToResult()
                            } else {
                                Toast.makeText(context, "Access denied!", Toast.LENGTH_SHORT).show()
                                rhythmEncoder.clear()
                            }
                        }
                    }, 2010)

                    return true
                }
                return false
            }
        })
    }

    // returns true if password is correct
    private fun evaluateRhythmRMSE(
        userInput: RhythmEncoder,
        password: RhythmEncoder,
        threshold: Int
    ): Boolean {
        // get rhythm codes to same length
        val lenInput = userInput.getRhythmCode().size
        val lenPswd = password.getRhythmCode().size

        if (lenInput != lenPswd) return false

        //calculate Root Mean Squared Error (=distance between vectors)
        val diff = arrayListOf<Long>()
        for (i in 0 until lenInput) {
            val temp = userInput.getRhythmCode().get(i) - password.getRhythmCode().get(i)
            diff.add(temp * temp)
        }
        val error = sqrt(diff.sum().toDouble() / diff.size).toInt()

        var test = ""
        for(e in diff) {
            test = test + e + ", "
        }

        Log.i("PASSWORD",""+password.display())
        Log.i("DIFF",""+test)
        Log.i("ACCESS INFO", "ERROR: " + error + ", THRESHOLD: "+threshold)

        return error < threshold
    }

    private fun evaluateRhythmPercentage(
        userInput: RhythmEncoder,
        password: RhythmEncoder,
        threshold: Double
    ): Boolean {
        // get rhythm codes to same length
        val lenInput = userInput.getRhythmCode().size
        val lenPswd = password.getRhythmCode().size

        if (lenInput != lenPswd) return false

        var meanPercentageError: Double = 0.0

        //calculate error (=distance between vectors)
        val diff = arrayListOf<Double>()
        for (i in 0 until lenInput) {
            val temp = userInput.getRhythmCode().get(i) - password.getRhythmCode().get(i)
            val tempPerc = abs(temp) / password.getRhythmCode().get(i).toDouble()
            diff.add(tempPerc * tempPerc)
        }

        meanPercentageError += (diff.sum() / diff.size.toDouble())

        var test = ""
        for(e in diff) {
            test = test + e + ", "
        }

        Log.i("PASSWORD",""+password.display())
        Log.i("DIFF",""+test)
        Log.i("ACCESS INFO", "ERROR: " + meanPercentageError + ", THRESHOLD: "+threshold)

        return meanPercentageError < threshold
    }

    private fun animateBigger(v: View?) {
        v?.animate()!!
            .scaleY(1.1f)
            .scaleX(1.1f)
            .setDuration(20L)
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
            navToResult()
        })

        // observer for button to navigation to setup fragment
        viewModel.navigateToSetupFragment.observe(viewLifecycleOwner, EventObserver {
            navToSetup()
        })

        // observer for button to navigation to about fragment
        viewModel.navigateToAboutFragment.observe(viewLifecycleOwner, EventObserver {
            navToAbout()
        })
    }

    fun navOk(): Boolean {
        return findNavController().currentDestination?.id == R.id.loginFragment
    }

    fun navToResult() {
        if(navOk()) findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToResultFragment())
    }

    fun navToSetup() {
        if(navOk()) findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSetupFragment())
    }

    fun navToAbout() {
        if(navOk()) findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAboutFragment())
    }

}