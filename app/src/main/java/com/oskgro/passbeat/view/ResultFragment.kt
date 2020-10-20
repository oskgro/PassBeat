package com.oskgro.passbeat.view

//code adapted from Coding with Sara https://codingwithsara.com/kotlin-android-studio-tutorial-the-simple-music-player/

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.oskgro.passbeat.R
import com.oskgro.passbeat.databinding.FragmentResultBinding
import com.oskgro.passbeat.util.EventObserver
import com.oskgro.passbeat.viewModel.ResultViewModel
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment : Fragment() {
    lateinit var binding: FragmentResultBinding
    lateinit var viewModel: ResultViewModel
    val contentView = R.layout.fragment_result

    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, contentView, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(ResultViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        super.onViewCreated(view, savedInstanceState)

        mp = MediaPlayer.create(context, R.raw.shave_haircut)
        mp.isLooping = true
        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration


        // Position Bar
        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        // Thread
        Thread(Runnable {
            while (mp != null) {
                try {
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what

            // Update positionBar
            positionBar.progress = currentPosition

            // Update Labels
            var elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = elapsedTime

            var remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    private fun setupObservers(){
        //play button
        viewModel.playBtnEvent.observe(viewLifecycleOwner, EventObserver {
            if (mp.isPlaying) {
                // Stop
                mp.pause()
                playBtn.setBackgroundResource(R.drawable.play)

            } else {
                // Start
                mp.start()
                playBtn.setBackgroundResource(R.drawable.stop)
            }
        })
    }
}