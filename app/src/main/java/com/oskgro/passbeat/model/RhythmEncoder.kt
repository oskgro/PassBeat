package com.oskgro.passbeat.model

class RhythmEncoder {

    private var rhythmCode = arrayListOf<Int>()

    fun addSegment(length: Int) {
        rhythmCode.add(length)
    }

    fun getRhythmCode(): ArrayList<Int> {
        return rhythmCode
    }

    fun setRhythmCode(code: ArrayList<Int>) {
        rhythmCode = code
    }

}