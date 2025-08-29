package xyz.akimlc.themetool.ui.compoent

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.extra.SuperDialog
import xyz.akimlc.themetool.utils.PreferenceUtil

@Composable
fun UpdateDialog(
    isShow: MutableState<Boolean>,
    versionName: String,
    changelog: String,
    downloadUrl: String,
    isManual: Boolean = false
) {
    val context = LocalContext.current
    var isChecked by rememberSaveable { mutableStateOf(false) }

    SuperDialog(
        show = isShow,
        title = "发现新版本！",
        summary = versionName,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 更新日志
            MarkdownText(md = changelog)

            Spacer(Modifier.height(6.dp))

            if (!isManual) {
                Card {
                    SuperCheckbox(
                        title = "不再提示",
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                            PreferenceUtil.setBoolean("ignore_update_$versionName", it)
                        },
                    )
                }
            }
            Row(modifier = Modifier.padding(top = 8.dp)) {
                TextButton(
                    text = "取消",
                    onClick = { isShow.value = false },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                TextButton(
                    text = "下载",
                    onClick = {
                        isShow.value = false
                        val intent = Intent(Intent.ACTION_VIEW, downloadUrl.toUri())
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    }
}

@Composable
fun MarkdownText(md: String) {
    val lines = md.lines() // 自动按 \r\n 或 \n 分割
    Column(modifier = Modifier.padding(4.dp)) {
        lines.forEach { line ->
            when {
                line.startsWith("##### ") -> Text(
                    text = line.removePrefix("##### "),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                line.startsWith("#####") -> Text(
                    text = line.removePrefix("#####"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                line.startsWith("- ") -> Text(
                    text = "• ${line.removePrefix("- ")}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 16.dp, bottom = 2.dp)
                )

                else -> Text(
                    text = line,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}