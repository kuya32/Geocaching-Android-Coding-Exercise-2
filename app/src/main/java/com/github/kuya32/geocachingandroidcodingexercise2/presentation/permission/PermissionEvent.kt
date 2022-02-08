package com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission

sealed class PermissionEvent {
    object OkRationale : PermissionEvent()
    object NopeRationale : PermissionEvent()
    object OpenSettings : PermissionEvent()
}
