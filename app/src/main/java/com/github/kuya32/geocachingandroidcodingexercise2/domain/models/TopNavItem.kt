package com.github.kuya32.geocachingandroidcodingexercise2.domain.models

import androidx.compose.ui.graphics.vector.ImageVector

data class TopNavItem(
    val name: String,
    val action: () -> Unit,
    val icon: ImageVector? = null
)
