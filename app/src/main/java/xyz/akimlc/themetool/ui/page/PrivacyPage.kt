package xyz.akimlc.themetool.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import xyz.akimlc.themetool.R

@Composable
fun PrivacyPage(
    isShow: MutableState<Boolean>,
    onAgree: () -> Unit,
    onCancel: () -> Unit
) {
    val darkColor = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    Box(
        modifier = Modifier
            .background(darkColor.background)
            .padding(24.dp)
            .padding(top = 12.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkColor.surface, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.privacy_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )


            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.privacy_content),
                    fontSize = 14.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    text = stringResource(R.string.button_cancel),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCancel()
                    }
                )
                Spacer(Modifier.width(12.dp))
                TextButton(
                    text = stringResource(R.string.button_confirm),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    onClick = {
                        onAgree()

                    }
                )
            }
        }
    }
}
