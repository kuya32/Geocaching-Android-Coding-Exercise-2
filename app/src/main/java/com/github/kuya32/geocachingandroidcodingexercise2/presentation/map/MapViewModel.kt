package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.PermissionRequest
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.PermissionEvent
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(

): ViewModel() {


}