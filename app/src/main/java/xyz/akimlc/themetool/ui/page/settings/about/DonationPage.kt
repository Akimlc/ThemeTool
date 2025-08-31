package xyz.akimlc.themetool.ui.page.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Cancel
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.AppScaffold

@Composable
fun DonationPage(navController: NavController) {
    AppScaffold(
        title = stringResource(R.string.title_donation),
        navController = navController
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
                        text = stringResource(R.string.donation_tip),
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
                    contentDescription = stringResource(R.string.donate_qr_wechat),
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
                    contentDescription = stringResource(R.string.donate_qr_alipay),
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