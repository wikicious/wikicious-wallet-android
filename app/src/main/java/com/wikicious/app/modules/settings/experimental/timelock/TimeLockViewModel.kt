package com.wikicious.app.modules.settings.experimental.timelock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wikicious.app.core.ILocalStorage

class TimeLockViewModel(private val localStorage: ILocalStorage) : ViewModel() {

    var timeLockActivated by mutableStateOf(localStorage.isLockTimeEnabled)
        private set

    fun setActivated(activated: Boolean) {
        localStorage.isLockTimeEnabled = activated
        timeLockActivated = activated
    }

}
