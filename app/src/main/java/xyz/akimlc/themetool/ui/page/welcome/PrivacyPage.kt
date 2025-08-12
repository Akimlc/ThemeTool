package xyz.akimlc.themetool.ui.page.welcome

import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import xyz.akimlc.themetool.R

@Composable
fun PrivacyPage(
    pagerState: PagerState
) {
    val scrollState = rememberScrollState()
    val activity = LocalActivity.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Icon(
            painter = painterResource(R.drawable.ic_privacy),
            contentDescription = "Permission",
            tint = Color(0xFF3482FF),
            modifier = Modifier.size(90.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "隐私政策",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Box(modifier = Modifier.verticalScroll(scrollState).padding(16.dp)) {
                Text(
                    text = "欢迎使用本应用（以下简称“本App”）。我们重视您的个人信息保护，请您阅读以下内容，了解我们如何收集和使用信息：\n\n" +
                            "【1. 收集的信息】\n" +
                            "- 设备信息（如型号、系统版本等）\n" +
                            "- 使用数据（如启动次数、崩溃信息等）\n" +
                            "- 通过第三方 SDK（如友盟）收集的匿名统计数据\n\n" +
                            "【2. 信息用途】\n" +
                            "- 用于优化产品与服务体验\n" +
                            "- 进行数据分析和故障排查\n" +
                            "- 遵守相关法律法规\n\n" +
                            "【3. 信息保护】\n" +
                            "我们会采取合理措施保障信息安全，防止泄露、丢失或非法访问。\n\n" +
                            "【4. 您的权利】\n" +
                            "- 有权选择是否同意本政策\n" +
                            "- 可随时停止使用本App\n" +
                            "- 如有疑问，可随时联系我们\n\n" +
                            "【5. 政策更新】\n" +
                            "我们可能适时更新隐私政策，变更内容将在App中通知。",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))


        TextButton(
            text = "拒绝",
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                activity?.finish()
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(3)
                }
            },
            colors = ButtonDefaults.textButtonColorsPrimary(),
            text = "同意",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}