package com.wikicious.app.core.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SubscriptionManager {

    var authToken: String? = ""

    private val _authTokenFlow = MutableStateFlow(authToken)
    val authTokenFlow: StateFlow<String?> = _authTokenFlow

    fun hasSubscription(): Boolean {
        return true
    }

}