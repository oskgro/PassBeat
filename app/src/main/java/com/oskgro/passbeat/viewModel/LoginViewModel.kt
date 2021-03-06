package com.oskgro.passbeat.viewModel

import android.app.usage.UsageEvents
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oskgro.passbeat.util.Event

class LoginViewModel: ViewModel() {

    private val _navigateToResultFragment = MutableLiveData<Event<Unit>>()
    val navigateToResultFragment: LiveData<Event<Unit>> = _navigateToResultFragment

    private val _navigateToSetupFragment = MutableLiveData<Event<Unit>>()
    val navigateToSetupFragment: LiveData<Event<Unit>> = _navigateToSetupFragment

    private val _navigateToAboutFragment = MutableLiveData<Event<Unit>>()
    val navigateToAboutFragment: LiveData<Event<Unit>> = _navigateToAboutFragment

    private val _appIsMuted = MutableLiveData(false)
    val appIsMuted: MutableLiveData<Boolean> = _appIsMuted

    fun mainButtonTapped(view: View) {
        view.animate().scaleX(2f).scaleY(2f).setDuration(1000L).start()
    }

    fun goToResultFragment() {
        _navigateToResultFragment.value = Event(Unit)
    }

    fun forgottenRhythm() {
        _navigateToSetupFragment.value = Event(Unit)
    }

    // navigation to setup fragment for testing
    fun goToSetupFragment() {
        _navigateToSetupFragment.value = Event(Unit)
    }

    // navigation to about fragment for testing
    fun goToAboutFragment() {
        _navigateToAboutFragment.value = Event(Unit)
    }

    fun muteButtonClicked() {
        appIsMuted.value = !appIsMuted.value!!
    }

}