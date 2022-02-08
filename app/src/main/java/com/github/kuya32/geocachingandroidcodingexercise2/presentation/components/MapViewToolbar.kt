package com.github.kuya32.geocachingandroidcodingexercise2.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.kuya32.geocachingandroidcodingexercise2.R

@Composable
fun MapViewToolbar(
    modifier: Modifier,
    onNavigateToPinClick: () -> Unit,
    onNavigateToUserClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.geocaching))
        },
        modifier = modifier,
        actions = {
            IconButton(
                onClick = { onNavigateToPinClick() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pinned_location),
                    contentDescription = "Navigate to pinned location"
                )
            }
            IconButton(
                onClick = { onNavigateToUserClick() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_my_location),
                    contentDescription = "Navigate to location button"
                )
            }
        },
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground
    )
}