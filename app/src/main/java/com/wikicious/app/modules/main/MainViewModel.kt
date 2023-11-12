package com.wikicious.app.modules.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.collectWith
import com.wikicious.app.core.IAccountManager
import com.wikicious.app.core.IBackupManager
import com.wikicious.app.core.ILocalStorage
import com.wikicious.app.core.IRateAppManager
import com.wikicious.app.core.ITermsManager
import com.wikicious.app.core.managers.ActiveAccountState
import com.wikicious.app.core.managers.ReleaseNotesManager
import com.wikicious.app.entities.Account
import com.wikicious.app.entities.AccountType
import com.wikicious.app.entities.LaunchPage
import com.wikicious.app.modules.main.MainModule.MainNavigation
import com.wikicious.app.modules.walletconnect.version2.WC2Manager
import com.wikicious.app.modules.walletconnect.version2.WC2SessionManager
import io.horizontalsystems.core.IPinComponent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val pinComponent: IPinComponent,
    rateAppManager: IRateAppManager,
    private val backupManager: IBackupManager,
    private val termsManager: ITermsManager,
    private val accountManager: IAccountManager,
    private val releaseNotesManager: ReleaseNotesManager,
    private val localStorage: ILocalStorage,
    wc2SessionManager: WC2SessionManager,
    wc2Manager: WC2Manager,
    private val wcDeepLink: String?
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private var wc2PendingRequestsCount = 0
    private var marketsTabEnabled = localStorage.marketsTabEnabledFlow.value
    private var transactionsEnabled = isTransactionsTabEnabled()
    private var settingsBadge: MainModule.BadgeType? = null
    private val launchPage: LaunchPage
        get() = localStorage.launchPage ?: LaunchPage.Auto

    private var currentMainTab: MainNavigation?
        get() = localStorage.mainTab
        set(value) {
            localStorage.mainTab = value
        }

    private var relaunchBySettingChange: Boolean
        get() = localStorage.relaunchBySettingChange
        set(value) {
            localStorage.relaunchBySettingChange = value
        }

    private val items: List<MainNavigation>
        get() = if (marketsTabEnabled) {
            listOf(
                MainNavigation.Market,
                MainNavigation.Balance,
                MainNavigation.Transactions,
                MainNavigation.Settings,
            )
        } else {
            listOf(
                MainNavigation.Balance,
                MainNavigation.Transactions,
                MainNavigation.Settings,
            )
        }

    private var selectedPageIndex = getPageIndexToOpen()
    private var mainNavItems = navigationItems()
    private var showRateAppDialog = false
    private var contentHidden = pinComponent.isLocked
    private var showWhatsNew = false
    private var activeWallet = accountManager.activeAccount
    private var wcSupportState: WC2Manager.SupportState? = null
    private var torEnabled = localStorage.torEnabled

    val wallets: List<Account>
        get() = accountManager.accounts.filter { !it.isWatchAccount }

    val watchWallets: List<Account>
        get() = accountManager.accounts.filter { it.isWatchAccount }

    var uiState by mutableStateOf(
        MainModule.UiState(
            selectedPageIndex = selectedPageIndex,
            mainNavItems = mainNavItems,
            showRateAppDialog = showRateAppDialog,
            contentHidden = contentHidden,
            showWhatsNew = showWhatsNew,
            activeWallet = activeWallet,
            wcSupportState = wcSupportState,
            torEnabled = torEnabled
        )
    )
        private set

    init {
        localStorage.marketsTabEnabledFlow.collectWith(viewModelScope) {
            marketsTabEnabled = it
            syncNavigation()
        }

        termsManager.termsAcceptedSignalFlow.collectWith(viewModelScope) {
            updateSettingsBadge()
        }

        wc2SessionManager.pendingRequestCountFlow.collectWith(viewModelScope) {
            wc2PendingRequestsCount = it
            updateSettingsBadge()
        }

        rateAppManager.showRateAppFlow.collectWith(viewModelScope) {
            showRateAppDialog = it
            syncState()
        }

        disposables.add(backupManager.allBackedUpFlowable.subscribe {
            updateSettingsBadge()
        })

        disposables.add(pinComponent.pinSetFlowable.subscribe {
            updateSettingsBadge()
        })

        disposables.add(accountManager.accountsFlowable.subscribe {
            updateTransactionsTabEnabled()
            updateSettingsBadge()
        })

        viewModelScope.launch {
            accountManager.activeAccountStateFlow.collect {
                if (it is ActiveAccountState.ActiveAccount) {
                    updateTransactionsTabEnabled()
                }
            }
        }

        wcDeepLink?.let {
            wcSupportState = wc2Manager.getWalletConnectSupportState()
            syncState()
        }

        accountManager.activeAccountStateFlow.collectWith(viewModelScope) {
            (it as? ActiveAccountState.ActiveAccount)?.let { state ->
                activeWallet = state.account
                syncState()
            }
        }

        updateSettingsBadge()
        updateTransactionsTabEnabled()
        showWhatsNew()
    }

    private fun isTransactionsTabEnabled(): Boolean =
        !accountManager.isAccountsEmpty && accountManager.activeAccount?.type !is AccountType.Cex


    override fun onCleared() {
        disposables.clear()
    }

    fun whatsNewShown() {
        showWhatsNew = false
        syncState()
    }

    fun closeRateDialog() {
        showRateAppDialog = false
        syncState()
    }

    fun onSelect(account: Account) {
        accountManager.setActiveAccountId(account.id)
        activeWallet = account
        syncState()
    }

    fun onResume() {
        contentHidden = pinComponent.isLocked
        syncState()
    }

    fun onSelect(mainNavItem: MainNavigation) {
        if (mainNavItem != MainNavigation.Settings) {
            currentMainTab = mainNavItem
        }
        selectedPageIndex = items.indexOf(mainNavItem)
        syncNavigation()
    }

    private fun updateTransactionsTabEnabled() {
        transactionsEnabled = isTransactionsTabEnabled()
        syncNavigation()
    }

    fun wcSupportStateHandled() {
        wcSupportState = null
        syncState()
    }

    private fun syncState() {
        uiState = MainModule.UiState(
            selectedPageIndex = selectedPageIndex,
            mainNavItems = mainNavItems,
            showRateAppDialog = showRateAppDialog,
            contentHidden = contentHidden,
            showWhatsNew = showWhatsNew,
            activeWallet = activeWallet,
            wcSupportState = wcSupportState,
            torEnabled = torEnabled
        )
    }

    private fun navigationItems(): List<MainModule.NavigationViewItem> {
        return items.mapIndexed { index, mainNavItem ->
            getNavItem(mainNavItem, index == selectedPageIndex)
        }
    }

    private fun getNavItem(item: MainNavigation, selected: Boolean) = when (item) {
        MainNavigation.Market -> {
            MainModule.NavigationViewItem(
                mainNavItem = item,
                selected = selected,
                enabled = true,
            )
        }
        MainNavigation.Transactions -> {
            MainModule.NavigationViewItem(
                mainNavItem = item,
                selected = selected,
                enabled = transactionsEnabled,
            )
        }
        MainNavigation.Settings -> {
            MainModule.NavigationViewItem(
                mainNavItem = item,
                selected = selected,
                enabled = true,
                badge = settingsBadge
            )
        }
        MainNavigation.Balance -> {
            MainModule.NavigationViewItem(
                mainNavItem = item,
                selected = selected,
                enabled = true,
            )
        }
    }

    private fun getPageIndexToOpen(): Int {
        val page = when {
            wcDeepLink != null -> {
                MainNavigation.Settings
            }
            relaunchBySettingChange -> {
                relaunchBySettingChange = false
                MainNavigation.Settings
            }
            !marketsTabEnabled -> {
                MainNavigation.Balance
            }
            else -> when (launchPage) {
                LaunchPage.Market,
                LaunchPage.Watchlist -> MainNavigation.Market
                LaunchPage.Balance -> MainNavigation.Balance
                LaunchPage.Auto -> currentMainTab ?: MainNavigation.Balance
            }
        }
        return items.indexOf(page)
    }

    private fun syncNavigation() {
        mainNavItems = navigationItems()
        if (selectedPageIndex >= mainNavItems.size) {
            selectedPageIndex = mainNavItems.size - 1
        }
        syncState()
    }

    private fun showWhatsNew() {
        viewModelScope.launch {
            if (releaseNotesManager.shouldShowChangeLog()) {
                delay(2000)
                showWhatsNew = true
                syncState()
            }
        }
    }

    private fun updateSettingsBadge() {
        val showDotBadge =
            !(backupManager.allBackedUp && termsManager.allTermsAccepted && pinComponent.isPinSet) || accountManager.hasNonStandardAccount

        settingsBadge = if (wc2PendingRequestsCount > 0) {
            MainModule.BadgeType.BadgeNumber(wc2PendingRequestsCount)
        } else if (showDotBadge) {
            MainModule.BadgeType.BadgeDot
        } else {
            null
        }
        syncNavigation()
    }

}
