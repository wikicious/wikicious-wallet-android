package com.wikicious.app.modules.send

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.navigation.navGraphViewModels
import com.wikicious.app.R
import com.wikicious.app.core.App
import com.wikicious.app.core.BaseFragment
import com.wikicious.app.entities.Wallet
import com.wikicious.app.modules.amount.AmountInputModeModule
import com.wikicious.app.modules.amount.AmountInputModeViewModel
import com.wikicious.app.modules.send.binance.SendBinanceModule
import com.wikicious.app.modules.send.binance.SendBinanceScreen
import com.wikicious.app.modules.send.binance.SendBinanceViewModel
import com.wikicious.app.modules.send.bitcoin.SendBitcoinModule
import com.wikicious.app.modules.send.bitcoin.SendBitcoinNavHost
import com.wikicious.app.modules.send.bitcoin.SendBitcoinViewModel
import com.wikicious.app.modules.send.evm.SendEvmModule
import com.wikicious.app.modules.send.evm.SendEvmScreen
import com.wikicious.app.modules.send.evm.SendEvmViewModel
import com.wikicious.app.modules.send.evm.confirmation.EvmKitWrapperHoldingViewModel
import com.wikicious.app.modules.send.solana.SendSolanaModule
import com.wikicious.app.modules.send.solana.SendSolanaScreen
import com.wikicious.app.modules.send.solana.SendSolanaViewModel
import com.wikicious.app.modules.send.tron.SendTronModule
import com.wikicious.app.modules.send.tron.SendTronScreen
import com.wikicious.app.modules.send.tron.SendTronViewModel
import com.wikicious.app.modules.send.zcash.SendZCashModule
import com.wikicious.app.modules.send.zcash.SendZCashScreen
import com.wikicious.app.modules.send.zcash.SendZCashViewModel
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.parcelable
import io.horizontalsystems.marketkit.models.BlockchainType

class SendFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            try {
                val arguments = requireArguments()
                val wallet = arguments.parcelable<Wallet>(walletKey) ?: throw IllegalStateException("Wallet is Null!")
                val title = arguments.getString(titleKey) ?: ""
                val sendEntryPointDestId = arguments.getInt(sendEntryPointDestIdKey)
                val predefinedAddress = arguments.getString(predefinedAddressKey)

                val amountInputModeViewModel by navGraphViewModels<AmountInputModeViewModel>(R.id.sendXFragment) {
                    AmountInputModeModule.Factory(wallet.coin.uid)
                }

                when (wallet.token.blockchainType) {
                    BlockchainType.Bitcoin,
                    BlockchainType.BitcoinCash,
                    BlockchainType.ECash,
                    BlockchainType.Litecoin,
                    BlockchainType.Dash -> {
                        val factory = SendBitcoinModule.Factory(wallet, predefinedAddress)
                        val sendBitcoinViewModel by navGraphViewModels<SendBitcoinViewModel>(R.id.sendXFragment) {
                            factory
                        }
                        setContent {
                            SendBitcoinNavHost(
                                title,
                                findNavController(),
                                sendBitcoinViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId
                            )
                        }
                    }

                    is BlockchainType.BinanceChain -> {
                        val factory = SendBinanceModule.Factory(wallet, predefinedAddress)
                        val sendBinanceViewModel by navGraphViewModels<SendBinanceViewModel>(R.id.sendXFragment) {
                            factory
                        }
                        setContent {
                            SendBinanceScreen(
                                title,
                                findNavController(),
                                sendBinanceViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId
                            )
                        }
                    }

                    BlockchainType.Zcash -> {
                        val factory = SendZCashModule.Factory(wallet, predefinedAddress)
                        val sendZCashViewModel by navGraphViewModels<SendZCashViewModel>(R.id.sendXFragment) {
                            factory
                        }
                        setContent {
                            SendZCashScreen(
                                title,
                                findNavController(),
                                sendZCashViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId
                            )
                        }
                    }

                    BlockchainType.Ethereum,
                    BlockchainType.BinanceSmartChain,
                    BlockchainType.Polygon,
                    BlockchainType.Avalanche,
                    BlockchainType.Optimism,
                    BlockchainType.Gnosis,
                    BlockchainType.Fantom,
                    BlockchainType.ArbitrumOne -> {
                        val factory = SendEvmModule.Factory(wallet, predefinedAddress)
                        val evmKitWrapperViewModel by navGraphViewModels<EvmKitWrapperHoldingViewModel>(
                            R.id.sendXFragment
                        ) { factory }
                        val initiateLazyViewModel = evmKitWrapperViewModel //needed in SendEvmConfirmationFragment
                        val sendEvmViewModel by navGraphViewModels<SendEvmViewModel>(R.id.sendXFragment) { factory }
                        setContent {
                            SendEvmScreen(
                                title,
                                findNavController(),
                                sendEvmViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId
                            )
                        }
                    }

                    BlockchainType.Solana -> {
                        val factory = SendSolanaModule.Factory(wallet, predefinedAddress)
                        val sendSolanaViewModel by navGraphViewModels<SendSolanaViewModel>(R.id.sendXFragment) { factory }
                        setContent {
                            SendSolanaScreen(
                                title,
                                findNavController(),
                                sendSolanaViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId
                            )
                        }
                    }

                    BlockchainType.Tron -> {
                        val factory = SendTronModule.Factory(wallet, predefinedAddress)
                        val sendTronViewModel by navGraphViewModels<SendTronViewModel>(R.id.sendXFragment) { factory }
                        setContent {
                            SendTronScreen(
                                title,
                                findNavController(),
                                sendTronViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId
                            )
                        }
                    }

                    else -> {}
                }
            } catch (t: Throwable) {
                Toast.makeText(
                    App.instance, t.message ?: t.javaClass.simpleName, Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        private const val walletKey = "walletKey"
        private const val sendEntryPointDestIdKey = "sendEntryPointDestIdKey"
        private const val titleKey = "titleKey"
        private const val predefinedAddressKey = "predefinedAddressKey"

        fun prepareParams(wallet: Wallet, title: String) = bundleOf(
            walletKey to wallet,
            titleKey to title
        )

        fun prepareParams(
            wallet: Wallet,
            sendEntryPointDestId: Int,
            title: String,
            predefinedAddress: String? = null
        ) = bundleOf(
            walletKey to wallet,
            sendEntryPointDestIdKey to sendEntryPointDestId,
            titleKey to title,
            predefinedAddressKey to predefinedAddress
        )
    }
}
