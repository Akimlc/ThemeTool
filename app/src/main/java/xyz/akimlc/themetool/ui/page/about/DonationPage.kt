package xyz.akimlc.themetool.ui.page.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Cancel
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar

@Composable
fun DonationPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            BackTopAppBar(
                title = "赞赏",
                scrollBehavior = scrollBehavior,
                navController = navController,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxHeight()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            //小Tips
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .padding(horizontal = 12.dp),
                    insideMargin = PaddingValues(vertical = 12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "如果你觉得本项目对你有帮助，欢迎请作者喝杯奶茶~\uD83D\uDE0A",
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, end = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Image(
                            modifier = Modifier
                                .padding(end = 24.dp)
                                .size(10.dp, 14.dp),
                            imageVector = MiuixIcons.Useful.Cancel,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariantActions)
                        )
                    }
                }
            }

            //二维码展示界面
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.mipmap.donate_wechat),
                        contentDescription = "微信赞赏二维码",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                    Image(
                        painter = painterResource(R.mipmap.donate_alipay),
                        contentDescription = "微信赞赏二维码",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
        }

    }
}