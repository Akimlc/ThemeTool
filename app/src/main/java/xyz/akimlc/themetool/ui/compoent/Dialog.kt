package xyz.akimlc.themetool.ui.compoent

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog

@Composable
fun PrivacyDialog(
    isShow: MutableState<Boolean>,
    onAgree: () -> Unit,
    onCancel: () -> Unit,
) {
    SuperDialog(
        title = "隐私政策",
        show = isShow,
        onDismissRequest = { }
    ) {
        Text(
            text = "欢迎使用本应用（以下简称“本App”）。我们非常重视您的个人信息保护，请您仔细阅读以下隐私政策内容，了解我们如何收集、使用和保护您的信息。\n\n" +
                    "【1. 我们收集的信息】\n" +
                    " 设备信息（如设备型号、系统版本、唯一设备标识等）\n" +
                    " 日志信息（如使用时长、启动次数、崩溃信息等）\n" +
                    " 其他通过友盟统计 SDK 收集的匿名数据\n\n" +
                    "【2. 信息的用途】\n" +
                    " 优化和改进我们的产品和服务\n" +
                    " 进行数据分析和统计，提升用户体验\n" +
                    " 遵守法律法规要求\n\n" +
                    "【3. 信息保护】\n" +
                    "我们采取合理的技术和管理措施，保护您的个人信息安全，防止信息泄露、丢失或被非法访问。\n\n" +
                    "【4. 您的权利】\n" +
                    " 您有权选择是否同意本隐私政策\n" +
                    " 您可以随时停止使用本App\n" +
                    " 如有任何隐私相关的问题，欢迎联系我们\n\n" +
                    "【5. 隐私政策更新】\n" +
                    "我们可能会不时更新本隐私政策，更新内容会在本App中及时告知。\n"
            ,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                "取消",
                onClick = {
                    onCancel()
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            TextButton(
                "同意",
                onClick = {
                    onAgree()
                },
                colors = ButtonDefaults.textButtonColorsPrimary(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

