package com.mosugu.colorpickerlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mosugu.colorpicker.Data
import com.mosugu.colorpickerlibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Data.getLiveData().observe(this, {binding.tv.setBackgroundColor(it)})

    }
}