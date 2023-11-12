package com.wikicious.app.modules.settings.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.App
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.components.ButtonPrimaryDefault
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.VSpacer
import com.wikicious.app.ui.extensions.BaseComposableBottomSheetFragment
import com.wikicious.app.ui.extensions.BottomSheetHeader
import com.wikicious.app.ui.helpers.TextHelper
import io.horizontalsystems.core.findNavController

class ContactOptionsDialog : BaseComposableBottomSheetFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                ContactOptionsScreen(
                    findNavController(),
                    App.appConfigProvider.reportEmail
                ) { close() }
            }
        }
    }
}

@Composable
private fun ContactOptionsScreen(
    navController: NavController,
    reportEmail: String,
    onCloseClick: () -> Unit
) {
    val context = LocalContext.current
    ComposeAppTheme {
        BottomSheetHeader(
            iconPainter = painterResource(R.drawable.ic_mail_24),
            iconTint = ColorFilter.tint(ComposeAppTheme.colors.jacob),
            title = stringResource(R.string.SettingsContact_Title),
            onCloseClick = onCloseClick
        ) {
            VSpacer(24.dp)
            ButtonPrimaryYellow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                title = stringResource(R.string.Settings_Contact_ViaEmail),
                onClick = {
                    sendEmail(reportEmail, context)
                }
            )
            VSpacer(12.dp)
            ButtonPrimaryDefault(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                title = stringResource(R.string.Settings_Contact_ViaTelegram),
                onClick = {
                    navController.slideFromRight(R.id.personalSupportFragment)
                }
            )
            VSpacer(24.dp)
        }
    }
}

private fun sendEmail(recipient: String, context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        TextHelper.copyText(recipient)
    }
}