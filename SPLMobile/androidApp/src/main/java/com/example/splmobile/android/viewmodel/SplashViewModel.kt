package com.example.splmobile.android.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splmobile.android.data.DataStoreRepository
import com.example.splmobile.android.ui.navigation.Screen
import kotlinx.coroutines.launch
import javax.inject.Inject



class SplashViewModel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {

    //Decides when to close splash screen
    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<String> = mutableStateOf(Screen.Onboarding.route)
    val startDestination: State<String> = _startDestination

    //Decides which screen to open with the information on the DataStoreRepository
    init {
        viewModelScope.launch {
            repository.readOnBoardingState().collect { completed ->
                if (completed) {
                    _startDestination.value = Screen.Authentication.route
                } else {
                    _startDestination.value = Screen.Onboarding.route
                }
            }
            _isLoading.value = false
        }
    }

}