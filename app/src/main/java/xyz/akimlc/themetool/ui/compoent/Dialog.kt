package xyz.akimlc.themetool.ui.compoent

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import xyz.akimlc.themetool.R

@Composable
fun PrivacyDialog(
    isShow: MutableState<Boolean>,
    onAgree: () -> Unit,
    onCancel: () -> Unit,
) {
    SuperDialog(
        title = stringResource(id = R.string.privacy_dialog_title),
        show = isShow,
        onDismissRequest = { }
    ) {
        Text(
            text = stringResource(id = R.string.privacy_dialog_content),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                stringResource(id = R.string.privacy_cancel),
                onClick = {
                    onCancel()
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            TextButton(
                stringResource(id = R.string.privacy_agree),
                onClick = {
                    onAgree()
                },
                colors = ButtonDefaults.textButtonColorsPrimary(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}