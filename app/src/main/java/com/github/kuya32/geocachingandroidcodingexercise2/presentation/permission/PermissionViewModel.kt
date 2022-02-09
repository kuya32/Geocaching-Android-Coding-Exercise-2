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

/* The MapViewModel consists of variables and functions that handle all the business logic and
states associated with the permission view.
*/
@HiltViewModel
class PermissionViewModel @Inject constructor(

) : ViewModel() {

    private val _doNotShowRationale = mutableStateOf(false)
    val doNotShowRationale: State<Boolean> = _doNotShowRationale

    private val _isPermissionsLaunched = mutableStateOf(false)

    private val _isSettingsLaunched = mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEventPermissions(event: PermissionEvent) {
        when (event) {
            /* Event is triggered when user clicks the "Ok" button, which then emits a boolean to
            launch location permission request to the user.
             */
            is PermissionEvent.OkRationale -> {
                _isPermissionsLaunched.value = true
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.LaunchPermissions(_isPermissionsLaunched.value)
                    )
                }
            }
            /* Event is triggered when user clicks the "Nope" text, which then shows SettingDialog
            composable.
             */
            is PermissionEvent.NopeRationale -> {
                _doNotShowRationale.value = true
            }
            /* Event is triggered when user clicks the "Open Settings" button, which then emits a
            boolean to launch the application settings.
             */
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