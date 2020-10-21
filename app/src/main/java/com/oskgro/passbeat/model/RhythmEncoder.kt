package com.oskgro.passbeat.model

import android.content.Context
import android.widget.Toast
import kotlin.coroutines.coroutineContext

class RhythmEncoder {

    private var rhythmCode = arrayListOf<Long>()

    fun addSegment(length: Long) {
        rhythmCode.add(length)
    }

    fun getCode(index: Int): Long {
        return rhythmCode.get(index)
    }

    fun getRhythmCode(): ArrayList<Long> {
        return rhythmCode
    }

    fun clear() {
        rhythmCode = arrayListOf()
    }

    fun isEmpty(): Boolean {
        return rhythmCode.isEmpty()
    }

    fun size(): Int {
        return rhythmCode.size
    }

    fun display(): String {
        var str = ""
        for(seg in rhythmCode) {
            str = str + seg.toString() + ", "
        }
        return str
    }

    // extend rhythm code with zeros
    fun extendRhythmCode(length: Int){
        val extention = LongArray(length){0}.asList()
        rhythmCode.addAll(extention)
    }

}