package com.wikicious.app.modules.manageaccount.backupkey

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wikicious.app.entities.Account
import com.wikicious.app.entities.AccountType
import com.wikicious.app.modules.manageaccount.recoveryphrase.RecoveryPhraseModule

class BackupKeyViewModel(val account: Account) : ViewModel() {

    var passphrase by mutableStateOf("")
        private set

    var wordsNumbered by mutableStateOf<List<RecoveryPhraseModule.WordNumbered>>(listOf())
        private set

    init {
        if (account.type is AccountType.Mnemonic) {
            wordsNumbered = account.type.words.mapIndexed { index, word ->
                RecoveryPhraseModule.WordNumbered(word, index + 1)
            }
            passphrase = account.type.passphrase
        }
    }
}
