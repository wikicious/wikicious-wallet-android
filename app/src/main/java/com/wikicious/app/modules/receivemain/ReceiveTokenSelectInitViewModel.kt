package com.wikicious.app.modules.receivemain

import androidx.lifecycle.ViewModel
import com.wikicious.app.core.App
import com.wikicious.app.entities.Account

class ReceiveTokenSelectInitViewModel : ViewModel() {
    fun getActiveAccount(): Account? {
        return App.accountManager.activeAccount
    }
}
