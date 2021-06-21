package com.mosugu.colorpicker

import android.graphics.Color
import androidx.lifecycle.MutableLiveData


object Data {
    private var colorArgb = Color.WHITE
    private val liveData = MutableLiveData<Int>()

    fun getLiveData() : MutableLiveData<Int> = liveData

    fun setColorArgb(colorArgb : Int) {
        this.colorArgb = colorArgb
        liveData.value = colorArgb
    }

    fun getColorArgb() : Int = colorArgb
}