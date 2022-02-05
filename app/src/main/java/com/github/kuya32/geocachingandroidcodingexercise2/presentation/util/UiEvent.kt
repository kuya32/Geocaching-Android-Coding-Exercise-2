package com.github.kuya32.geocachingandroidcodingexercise2.presentation.util

import android.content.Intent
import androidx.compose.runtime.MutableState

sealed class UiEvent {
    data class LaunchPermissions(val permissionLaunched: Boolean): UiEvent()
    data class NavigateSettings(val settingsLaunched: Boolean): UiEvent()
}
