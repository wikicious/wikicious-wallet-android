package com.wikicious.app.modules.pin

import android.os.Parcelable
import androidx.compose.runtime.Composable
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.setNavigationResultX
import com.wikicious.app.modules.pin.ui.PinConfirm
import com.wikicious.app.ui.compose.ComposeAppTheme
import io.horizontalsystems.core.findNavController
import kotlinx.parcelize.Parcelize

class ConfirmPinFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        val navController = findNavController()
        ComposeAppTheme {
            PinConfirm(
                onSuccess = {
                    navController.setNavigationResultX(Result(true))
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }

    @Parcelize
    data class Result(val success: Boolean) : Parcelable
}
