package com.wikicious.app.modules.receivemain

import androidx.lifecycle.ViewModel
import com.wikicious.app.core.App

class NetworkSelectInitViewModel(coinUid: String) : ViewModel() {
    val fullCoin = App.marketKit.fullCoins(listOf(coinUid)).firstOrNull()
    val activeAccount = App.accountManager.activeAccount
}
