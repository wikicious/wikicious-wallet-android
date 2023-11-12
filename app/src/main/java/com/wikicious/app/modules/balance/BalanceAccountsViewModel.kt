package com.wikicious.app.modules.balance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.collectWith
import com.wikicious.app.core.IAccountManager
import com.wikicious.app.core.managers.ActiveAccountState
import com.wikicious.app.entities.AccountType

class BalanceAccountsViewModel(accountManager: IAccountManager) : ViewModel() {

    var balanceScreenState by mutableStateOf<BalanceScreenState?>(null)
        private set

    init {
        accountManager.activeAccountStateFlow.collectWith(viewModelScope) {
                handleAccount(it)
            }
    }

    private fun handleAccount(activeAccountState: ActiveAccountState) {
        when(activeAccountState) {
            ActiveAccountState.NotLoaded -> { }
            is ActiveAccountState.ActiveAccount -> {
                balanceScreenState = if (activeAccountState.account != null) {
                    BalanceScreenState.HasAccount(
                        AccountViewItem(
                            activeAccountState.account.isWatchAccount,
                            activeAccountState.account.name,
                            activeAccountState.account.id,
                            activeAccountState.account.type
                        )
                    )
                } else {
                    BalanceScreenState.NoAccount
                }
            }
        }
    }
}

data class AccountViewItem(
    val isWatchAccount: Boolean,
    val name: String = "",
    val id: String,
    val type: AccountType
)

sealed class BalanceScreenState() {
    class HasAccount(val accountViewItem: AccountViewItem) : BalanceScreenState()
    object NoAccount : BalanceScreenState()
}
