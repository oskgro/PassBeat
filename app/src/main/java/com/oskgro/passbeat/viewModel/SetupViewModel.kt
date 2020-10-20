package com.oskgro.passbeat.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SetupViewModel: ViewModel() {

    private val _currentlyEncoding = MutableLiveData(1)
    val currentlyEncoding: MutableLiveData<Int> = _currentlyEncoding

    fun undoButtonClicked() {
        if(currentlyEncoding.value!! > 1) currentlyEncoding.value = currentlyEncoding.value!! - 1
    }

}