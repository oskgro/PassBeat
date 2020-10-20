package com.oskgro.passbeat.model

class RhythmEncoder {

    private var rhythmCode = arrayListOf<Long>()

    fun addSegment(length: Long) {
        rhythmCode.add(length)
    }

    fun getRhythmCode(): ArrayList<Long> {
        return rhythmCode
    }

    fun setRhythmCode(code: ArrayList<Long>) {
        rhythmCode = code
    }

    fun clear() {
        rhythmCode = arrayListOf()
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