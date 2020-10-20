package com.oskgro.passbeat.util

import android.content.SharedPreferences
import android.util.Log
import com.oskgro.passbeat.model.RhythmEncoder

class SharedPreferencesHelper(private val preferences: SharedPreferences) {

    // Keys
    private val encodedRhythm = "rhythm"

    fun saveRhythmEncoding(code: RhythmEncoder) {
        val saved = preferences.getString(encodedRhythm, "")
        if(saved == "") {
            var str = ""
            for(i in 0 until code.size()) {
                str = str + code.getCode(i) + ","
            }
            str = str.substring(0, str.length - 1)
            Log.i("STRING! ", ""+str)
            preferences.edit().putString(encodedRhythm, str).apply()
        }

    }

    fun loadRhythmEncoding(): RhythmEncoder {
        val saved = preferences.getString(encodedRhythm, "")
        if(saved == "") return RhythmEncoder()
        val list: List<String>? = saved?.split(",")
        if(list != null) {
            val rhythmEncoder = RhythmEncoder()
            for(str in list) {
                rhythmEncoder.addSegment(str.toLong())
            }
            return rhythmEncoder
        }
        return RhythmEncoder()
    }

}