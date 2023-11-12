package com.wikicious.app.core.managers

import com.wikicious.app.core.ILocalStorage
import com.wikicious.app.core.ITermsManager
import com.wikicious.app.modules.settings.terms.TermsModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TermsManager(private val localStorage: ILocalStorage) : ITermsManager {

    private val _termsAcceptedFlow = MutableStateFlow(localStorage.termsAccepted)
    override val termsAcceptedSignalFlow = _termsAcceptedFlow.asStateFlow()
    override val terms = TermsModule.TermType.values().toList()

    override val allTermsAccepted: Boolean
        get() = localStorage.termsAccepted

    override fun acceptTerms() {
        localStorage.termsAccepted = true
        _termsAcceptedFlow.update { localStorage.termsAccepted }
    }

}
