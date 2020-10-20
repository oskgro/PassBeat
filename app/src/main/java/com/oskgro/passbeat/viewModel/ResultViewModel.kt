package com.oskgro.passbeat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oskgro.passbeat.util.Event

class ResultViewModel: ViewModel() {
    private val _playBtnEvent = MutableLiveData<Event<Unit>>()
    val playBtnEvent: LiveData<Event<Unit>> =_playBtnEvent


    fun playBtnClicked () {
        _playBtnEvent.value = Event(Unit)
    }
}