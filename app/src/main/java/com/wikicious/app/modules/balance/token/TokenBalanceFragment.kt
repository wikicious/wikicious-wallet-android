package com.wikicious.app.modules.balance.token

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import com.wikicious.app.R
import com.wikicious.app.core.App
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.entities.Wallet
import com.wikicious.app.modules.transactions.TransactionsModule
import com.wikicious.app.modules.transactions.TransactionsViewModel
import com.wikicious.app.ui.compose.ComposeAppTheme
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.parcelable

class TokenBalanceFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        val wallet = requireArguments().parcelable<Wallet>(WALLET_KEY)
        if (wallet == null) {
            Toast.makeText(App.instance, "Wallet is Null", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }
        val viewModel by viewModels<TokenBalanceViewModel> { TokenBalanceModule.Factory(wallet) }
        val transactionsViewModel by navGraphViewModels<TransactionsViewModel>(R.id.mainFragment) { TransactionsModule.Factory() }

        ComposeAppTheme {
            TokenBalanceScreen(
                viewModel,
                transactionsViewModel,
                findNavController()
            )
        }
    }

    companion object {
        private const val WALLET_KEY = "wallet_key"

        fun prepareParams(wallet: Wallet) = bundleOf(
            WALLET_KEY to wallet
        )
    }
}
