package com.wikicious.app.modules.settings.appearance

import com.wikicious.app.core.ILocalStorage
import com.wikicious.app.entities.LaunchPage
import com.wikicious.app.ui.compose.Select
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LaunchScreenService(private val localStorage: ILocalStorage) {
    private val screens = LaunchPage.values().asList()

    private val _optionsFlow = MutableStateFlow(
        Select(localStorage.launchPage ?: LaunchPage.Auto, screens)
    )
    val optionsFlow = _optionsFlow.asStateFlow()

    fun setLaunchScreen(launchPage: LaunchPage) {
        localStorage.launchPage = launchPage

        _optionsFlow.update {
            Select(launchPage, screens)
        }
    }
}
