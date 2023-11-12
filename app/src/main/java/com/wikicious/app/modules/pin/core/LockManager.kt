package com.wikicious.app.modules.pin.core

import com.wikicious.app.core.ILocalStorage
import com.wikicious.app.modules.settings.security.autolock.AutoLockInterval
import io.horizontalsystems.core.helpers.DateHelper
import java.util.Date

class LockManager(
    private val pinManager: PinManager,
    private val localStorage: ILocalStorage
) {

    var isLocked: Boolean = true
        private set
    private val lockTimeout = 60L
    private var appLastVisitTime: Long = 0
    private var keepUnlocked = false

    fun didEnterBackground() {
        if (isLocked) {
            return
        }

        appLastVisitTime = Date().time
    }

    fun willEnterForeground() {
        if (isLocked || !pinManager.isPinSet) {
            return
        }

        val autoLockInterval = localStorage.autoLockInterval
        val secondsAgo = DateHelper.getSecondsAgo(appLastVisitTime)

        if (keepUnlocked) {
            keepUnlocked = false
            if (autoLockInterval == AutoLockInterval.IMMEDIATE && secondsAgo < 60) {
                return
            }
        }

        if (secondsAgo >= autoLockInterval.intervalInSeconds) {
            isLocked = true
        }
    }

    fun onUnlock() {
        isLocked = false
    }

    fun updateLastExitDate() {
        appLastVisitTime = Date().time
    }

    fun lock() {
        isLocked = true
        appLastVisitTime = 0
    }

    fun keepUnlocked() {
        keepUnlocked = true
    }

}
