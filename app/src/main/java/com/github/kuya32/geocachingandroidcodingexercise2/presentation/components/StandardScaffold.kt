package com.github.kuya32.geocachingandroidcodingexercise2.presentation.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.kuya32.geocachingandroidcodingexercise2.R

@Composable
fun StandardScaffold(
    modifier: Modifier = Modifier,
    showFabButton: Boolean = false,
    state: ScaffoldState,
    onFabClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        scaffoldState = state,
        floatingActionButton = {
            if (showFabButton) {
                FloatingActionButton(
                    onClick = onFabClick,
                    backgroundColor = MaterialTheme.colors.primary,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_pin__on_location),
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
    ) {
        content()
    }
}