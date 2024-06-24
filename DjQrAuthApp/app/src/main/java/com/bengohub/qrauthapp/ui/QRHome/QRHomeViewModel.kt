package com.bengohub.qrauthapp.ui.QRHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QRHomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is QRHome Fragment"
    }
    val text: LiveData<String> = _text
}