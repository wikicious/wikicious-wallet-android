package com.wikicious.app.modules.backuplocal.terms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wikicious.app.R
import com.wikicious.app.modules.evmfee.ButtonsGroupWithShade
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.CellUniversalLawrenceSection
import com.wikicious.app.ui.compose.components.HsBackButton
import com.wikicious.app.ui.compose.components.HsCheckbox
import com.wikicious.app.ui.compose.components.RowUniversal
import com.wikicious.app.ui.compose.components.TextImportantWarning
import com.wikicious.app.ui.compose.components.VSpacer
import com.wikicious.app.ui.compose.components.subhead2_leah

@Composable
fun LocalBackupTermsScreen(
    onTermsAccepted: ()-> Unit,
    onBackClick: () -> Unit,
) {
    var termChecked by rememberSaveable { mutableStateOf(false) }

    ComposeAppTheme {
        Scaffold(
            backgroundColor = ComposeAppTheme.colors.tyler,
            topBar = {
                AppBar(
                    title = stringResource(R.string.LocalBackup_Title),
                    navigationIcon = {
                        HsBackButton(onClick = onBackClick)
                    },
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.weight(1f)) {
                    TextImportantWarning(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        text = stringResource(R.string.LocalBackup_TermsWarningText)
                    )
                    VSpacer(24.dp)
                    CellUniversalLawrenceSection(
                        listOf {
                            LocalBackupTerm(
                                text = stringResource(R.string.LocalBackup_Term1),
                                checked = termChecked,
                                onCheckedChange = { checked ->
                                    termChecked = checked
                                }
                            )
                        }
                    )
                }
                ButtonsGroupWithShade {
                    ButtonPrimaryYellow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        title = stringResource(R.string.Button_Continue),
                        enabled = termChecked,
                        onClick = onTermsAccepted,
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalBackupTerm(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = {
            onCheckedChange.invoke(checked.not())
        }
    ) {
        HsCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Spacer(Modifier.width(16.dp))
        subhead2_leah(text = text)
    }
}
