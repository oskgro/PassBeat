package com.oskgro.passbeat.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.oskgro.passbeat.BuildConfig

object InjectorUtils {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    fun injectSharedPreferences(context: Context): SharedPreferencesHelper {
        if(!::sharedPreferencesHelper.isInitialized) {
            sharedPreferencesHelper = SharedPreferencesHelper(
                context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
            )
        }
        return sharedPreferencesHelper
    }


}