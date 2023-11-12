package com.wikicious.app.modules.pin

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.modules.pin.ui.PinSet
import com.wikicious.app.ui.compose.ComposeAppTheme
import io.horizontalsystems.core.findNavController

class EditPinFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            PinSet(
                title = stringResource(R.string.EditPin_Title),
                description = stringResource(R.string.EditPin_NewPinInfo),
                dismissWithSuccess = { findNavController().popBackStack() },
                onBackPress = { findNavController().popBackStack() }
            )
        }
    }
}
