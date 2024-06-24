package com.bengohub.qrauthapp.ui.ApiSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ApiSettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is ApiSettings Fragment"
    }
    val text: LiveData<String> = _text
}