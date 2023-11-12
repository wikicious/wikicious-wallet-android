package com.wikicious.app.modules.send.solana

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.core.ISendSolanaAdapter
import com.wikicious.app.entities.Wallet
import com.wikicious.app.modules.amount.AmountValidator
import com.wikicious.app.modules.send.SendAmountAdvancedService
import com.wikicious.app.modules.xrate.XRateService
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import java.math.RoundingMode

object SendSolanaModule {

    class Factory(private val wallet: Wallet, private val predefinedAddress: String?) : ViewModelProvider.Factory {
        val adapter = (App.adapterManager.getAdapterForWallet(wallet) as? ISendSolanaAdapter) ?: throw IllegalStateException("SendSolanaAdapter is null")

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendSolanaViewModel::class.java -> {
                    val amountValidator = AmountValidator()
                    val coinMaxAllowedDecimals = wallet.token.decimals

                    val amountService = SendAmountAdvancedService(
                            adapter.availableBalance.setScale(coinMaxAllowedDecimals, RoundingMode.DOWN),
                            wallet.token,
                            amountValidator,
                    )
                    val addressService = SendSolanaAddressService(predefinedAddress)
                    val xRateService = XRateService(App.marketKit, App.currencyManager.baseCurrency)
                    val feeToken = App.coinManager.getToken(TokenQuery(BlockchainType.Solana, TokenType.Native)) ?: throw IllegalArgumentException()

                    SendSolanaViewModel(
                        wallet,
                        wallet.token,
                        feeToken,
                        adapter,
                        xRateService,
                        amountService,
                        addressService,
                        coinMaxAllowedDecimals,
                        App.contactsRepository
                    ) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

}