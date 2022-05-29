package com.example.splmobile.models

import kotlinx.coroutines.CoroutineScope

expect abstract class ViewModel() {
    val viewModelScope: CoroutineScope
    protected open fun onCleared()
}
