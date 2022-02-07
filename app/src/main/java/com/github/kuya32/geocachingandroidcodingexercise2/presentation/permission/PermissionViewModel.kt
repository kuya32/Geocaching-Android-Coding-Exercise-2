package com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(

): ViewModel() {

    private val _doNotShowRationale = mutableStateOf(false)
    val doNotShowRationale: State<Boolean> = _doNotShowRationale

    private val _isPermissionsLaunched = mutableStateOf(false)

    private val _isSettingsLaunched = mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEventPermissions(event: PermissionEvent) {
        when (event) {
            is PermissionEvent.OkRationale -> {
                _isPermissionsLaunched.value = true
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.LaunchPermissions(_isPermissionsLaunched.value)
                    )
                }
            }
            is PermissionEvent.NopeRationale -> {
                _doNotShowRationale.value = true
            }
            is PermissionEvent.OpenSettings -> {
                _isSettingsLaunched.value = true
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.LaunchPermissions(_isSettingsLaunched.value)
                    )
                }
            }
        }
    }
}