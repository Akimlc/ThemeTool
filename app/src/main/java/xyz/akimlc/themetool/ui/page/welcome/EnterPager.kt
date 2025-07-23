package xyz.akimlc.themetool.ui.page.welcome

import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import xyz.akimlc.themetool.utils.PreferenceUtil

@Composable
fun EnterPager(
    show: MutableState<Boolean>,
    pagerState: PagerState
) {
    val view = LocalView.current
    val activity = LocalActivity.current
    val context = LocalContext.current
    val go = remember { mutableStateOf(false) }
    val easing = CubicBezierEasing(.42f, 0f, 0.26f, .85f)
    val animatedY = animateDpAsState(
        targetValue = if (go.value) (-30).dp else 0.dp,
        animationSpec = tween(durationMillis = 1150, easing = easing),
        label = "alpha"
    )
    val animatedAlpha = animateFloatAsState(
        targetValue = if (go.value) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1150, easing = easing),
        label = "alpha"
    )
    LaunchedEffect(
        pagerState.currentPage
    ) {
        go.value = pagerState.currentPage==3
    }
    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 100.dp)
                .offset(x = 0.dp, y = animatedY.value)
                .alpha(animatedAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(color = colorScheme.onBackground)
                    ) {
                        append("Theme")
                    }
                    withStyle(
                        style = SpanStyle(color = Color(0xFF2856FF))
                    ) {
                        append("Tool")
                    }
                },
                fontSize = 39.sp,
                fontWeight = FontWeight(560),
                modifier = Modifier
            )
            Text(
                text = "设置完成",
                modifier = Modifier.padding(top = 20.dp)
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp),
            colors = ButtonDefaults.buttonColors(color = Color(0xFF3482FF)),
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                show.value = false
                PreferenceUtil.setBoolean("first_launch", true)
            }
        ) {
            Text(
                text = "进入软件",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}