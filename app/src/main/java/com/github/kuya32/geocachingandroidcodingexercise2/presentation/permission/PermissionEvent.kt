package com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission

// Class holds the events used in the permissions view triggered by the user.
sealed class PermissionEvent {
    object OkRationale : PermissionEvent()
    object NopeRationale : PermissionEvent()
    object OpenSettings : PermissionEvent()
}
