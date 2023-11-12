package com.wikicious.app.modules.settings.main

import com.wikicious.app.R
import com.wikicious.app.core.IAccountManager
import com.wikicious.app.core.IBackupManager
import com.wikicious.app.core.ITermsManager
import com.wikicious.app.core.managers.CurrencyManager
import com.wikicious.app.core.managers.LanguageManager
import com.wikicious.app.core.providers.AppConfigProvider
import com.wikicious.app.core.providers.Translator
import com.wikicious.app.entities.Currency
import com.wikicious.app.modules.walletconnect.version2.WC2Manager
import com.wikicious.app.modules.walletconnect.version2.WC2SessionManager
import io.horizontalsystems.core.IPinComponent
import io.horizontalsystems.core.ISystemInfoManager
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MainSettingsService(
    private val backupManager: IBackupManager,
    private val languageManager: LanguageManager,
    private val systemInfoManager: ISystemInfoManager,
    private val currencyManager: CurrencyManager,
    private val termsManager: ITermsManager,
    private val pinComponent: IPinComponent,
    private val wc2SessionManager: WC2SessionManager,
    private val wc2Manager: WC2Manager,
    private val accountManager: IAccountManager,
    private val appConfigProvider: AppConfigProvider
) {

    val appWebPageLink = appConfigProvider.appWebPageLink
    private val backedUpSubject = BehaviorSubject.create<Boolean>()
    val backedUpObservable: Observable<Boolean> get() = backedUpSubject

    private val pinSetSubject = BehaviorSubject.create<Boolean>()
    val pinSetObservable: Observable<Boolean> get() = pinSetSubject

    val termsAccepted by termsManager::allTermsAccepted
    val termsAcceptedFlow by termsManager::termsAcceptedSignalFlow

    private val baseCurrencySubject = BehaviorSubject.create<Currency>()
    val baseCurrencyObservable: Observable<Currency> get() = baseCurrencySubject

    private val walletConnectSessionCountSubject = BehaviorSubject.create<Int>()
    val walletConnectSessionCountObservable: Observable<Int> get() = walletConnectSessionCountSubject

    val hasNonStandardAccount: Boolean
        get() = accountManager.hasNonStandardAccount

    private var disposables: CompositeDisposable = CompositeDisposable()

    val appVersion: String
        get() {
            var appVersion = systemInfoManager.appVersion
            if (Translator.getString(R.string.is_release) == "false") {
                appVersion += " (${appConfigProvider.appBuild})"
            }

            return appVersion
        }

    val allBackedUp: Boolean
        get() = backupManager.allBackedUp

    val pendingRequestCountFlow by wc2SessionManager::pendingRequestCountFlow

    val walletConnectSessionCount: Int
        get() = wc2SessionManager.sessions.count()

    val currentLanguageDisplayName: String
        get() = languageManager.currentLanguageName

    val baseCurrency: Currency
        get() = currencyManager.baseCurrency

    val isPinSet: Boolean
        get() = pinComponent.isPinSet

    fun start() {
        disposables.add(backupManager.allBackedUpFlowable.subscribe {
            backedUpSubject.onNext(it)
        })

        disposables.add(wc2SessionManager.sessionsObservable.subscribe {
            walletConnectSessionCountSubject.onNext(walletConnectSessionCount)
        })

        disposables.add(currencyManager.baseCurrencyUpdatedSignal.subscribe {
            baseCurrencySubject.onNext(currencyManager.baseCurrency)
        })

        disposables.add(pinComponent.pinSetFlowable.subscribe {
            pinSetSubject.onNext(pinComponent.isPinSet)
        })
    }

    fun stop() {
        disposables.clear()
    }

    fun getWalletConnectSupportState(): WC2Manager.SupportState {
        return wc2Manager.getWalletConnectSupportState()
    }
}
