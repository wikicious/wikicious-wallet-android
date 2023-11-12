package com.wikicious.app.modules.send

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.navGraphViewModels
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.modules.amount.AmountInputModeViewModel
import com.wikicious.app.modules.send.binance.SendBinanceConfirmationScreen
import com.wikicious.app.modules.send.binance.SendBinanceViewModel
import com.wikicious.app.modules.send.bitcoin.SendBitcoinConfirmationScreen
import com.wikicious.app.modules.send.bitcoin.SendBitcoinViewModel
import com.wikicious.app.modules.send.solana.SendSolanaConfirmationScreen
import com.wikicious.app.modules.send.solana.SendSolanaViewModel
import com.wikicious.app.modules.send.tron.SendTronConfirmationScreen
import com.wikicious.app.modules.send.tron.SendTronViewModel
import com.wikicious.app.modules.send.zcash.SendZCashConfirmationScreen
import com.wikicious.app.modules.send.zcash.SendZCashViewModel
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.parcelable
import kotlinx.parcelize.Parcelize

class SendConfirmationFragment : BaseComposeFragment() {
    val amountInputModeViewModel by navGraphViewModels<AmountInputModeViewModel>(R.id.sendXFragment)

    @Composable
    override fun GetContent() {
        val arguments = requireArguments()
        val sendEntryPointDestId = arguments.getInt(sendEntryPointDestIdKey)

        when (arguments.parcelable<Type>(typeKey)) {
            Type.Bitcoin -> {
                val sendBitcoinViewModel by navGraphViewModels<SendBitcoinViewModel>(R.id.sendXFragment)

                SendBitcoinConfirmationScreen(
                    findNavController(),
                    sendBitcoinViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            Type.Bep2 -> {
                val sendBinanceViewModel by navGraphViewModels<SendBinanceViewModel>(R.id.sendXFragment)

                SendBinanceConfirmationScreen(
                    findNavController(),
                    sendBinanceViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            Type.ZCash -> {
                val sendZCashViewModel by navGraphViewModels<SendZCashViewModel>(R.id.sendXFragment)

                SendZCashConfirmationScreen(
                    findNavController(),
                    sendZCashViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            Type.Tron -> {
                val sendTronViewModel by navGraphViewModels<SendTronViewModel>(R.id.sendXFragment)
                SendTronConfirmationScreen(
                    findNavController(),
                    sendTronViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            Type.Solana -> {
                val sendSolanaViewModel by navGraphViewModels<SendSolanaViewModel>(R.id.sendXFragment)

                SendSolanaConfirmationScreen(
                    findNavController(),
                    sendSolanaViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            null -> Unit
        }
    }

    @Parcelize
    enum class Type : Parcelable {
        Bitcoin, Bep2, ZCash, Solana, Tron
    }

    companion object {
        private const val typeKey = "typeKey"
        private const val sendEntryPointDestIdKey = "sendEntryPointDestIdKey"

        fun prepareParams(type: Type, sendEntryPointDestId: Int) = bundleOf(
            typeKey to type,
            sendEntryPointDestIdKey to sendEntryPointDestId,
        )
    }
}
