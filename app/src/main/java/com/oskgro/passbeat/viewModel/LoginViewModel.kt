package com.oskgro.passbeat.viewModel

import android.app.usage.UsageEvents
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oskgro.passbeat.util.Event

class LoginViewModel: ViewModel() {

    private val _navigateToResultFragment = MutableLiveData<Event<Unit>>()
    val navigateToResultFragment: LiveData<Event<Unit>> = _navigateToResultFragment

    fun yellowButtonPressed() {

    }

/*
    fun goToResultFragment() {
        _navigateToResultFragment.value = Event(Unit)
    }
*/
}