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
import com.oskgro.passbeat.R
import com.oskgro.passbeat.databinding.FragmentSetupBinding
import com.oskgro.passbeat.model.RhythmEncoder
import com.oskgro.passbeat.util.InjectorUtils
import com.oskgro.passbeat.viewModel.SetupViewModel

class SetupFragment: Fragment() {
    lateinit var binding: FragmentSetupBinding
    lateinit var viewModel: SetupViewModel
    val contentView = R.layout.fragment_setup

    var encodingState: Int = 0 // 0 = not started, 1 = started listening
    var timestampMillis: Long = 0
    var rhythmEncoderList: List<RhythmEncoder> = listOf(
        RhythmEncoder(),
        RhythmEncoder(),
        RhythmEncoder(),
        RhythmEncoder(),
        RhythmEncoder())
    var finalRhythmEncoder: RhythmEncoder = RhythmEncoder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, contentView, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(SetupViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListeners() {
        binding.yellowButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(event?.action == MotionEvent.ACTION_DOWN) {
                    animateBigger(v)
                    if(encodingState == 0) {
                        rhythmEncoderList[viewModel.currentlyEncoding.value!! - 1].clear()
                        encodingState = 1
                        timestampMillis = System.currentTimeMillis()
                    }
                    else if(encodingState == 1) {
                        val deltaTime = System.currentTimeMillis() - timestampMillis
                        rhythmEncoderList[viewModel.currentlyEncoding.value!! - 1].addSegment(deltaTime)
                        timestampMillis = System.currentTimeMillis()
                    }
                    return true
                }
                else if(event?.action == MotionEvent.ACTION_UP) {
                    animateToNormal(v)

                    val deltaTime = System.currentTimeMillis() - timestampMillis
                    rhythmEncoderList[viewModel.currentlyEncoding.value!! - 1].addSegment(deltaTime)
                    timestampMillis = System.currentTimeMillis()

                    Handler(Looper.getMainLooper()).postDelayed({
                        if((timestampMillis + 2000) < System.currentTimeMillis()){
                            encodingState = 0
                            if(viewModel.currentlyEncoding.value!! < 5) {
                                viewModel.currentlyEncoding.value =
                                    viewModel.currentlyEncoding.value!! + 1
                            }
                            else {
                                for(e in rhythmEncoderList) {
                                    Log.i("DISPLAY","Element: " + e.display())
                                }
                                compileAverageRhythm()
                            }
                        }
                    }, 2010)

                    return true
                }
                return false
            }
        })
    }

    private fun compileAverageRhythm() {
        finalRhythmEncoder.clear()
        val length = rhythmEncoderList[0].size()
        if( rhythmEncoderList[1].size() == length &&
            rhythmEncoderList[2].size() == length &&
            rhythmEncoderList[3].size() == length &&
            rhythmEncoderList[4].size() == length) {
            for(i in 0 until length) {
                var sum: Long = 0
                for(rhythm in rhythmEncoderList) {
                    sum += rhythm.getCode(i)
                }
                finalRhythmEncoder.addSegment((sum / 5))
            }
            Log.i("FINAL! ",""+finalRhythmEncoder.display())
            InjectorUtils.injectSharedPreferences(requireContext()).saveRhythmEncoding(finalRhythmEncoder)
        }
        else {
            Toast.makeText(context, "Error! The rhythms were of different lengths!", Toast.LENGTH_LONG).show()
            viewModel.currentlyEncoding.value = 1
        }
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
}