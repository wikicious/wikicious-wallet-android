package com.wikicious.app.ui.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import io.horizontalsystems.core.helpers.HudHelper

@Composable
fun SnackbarError(errorMessage: String) {
    HudHelper.showErrorMessage(LocalView.current, errorMessage)
}
