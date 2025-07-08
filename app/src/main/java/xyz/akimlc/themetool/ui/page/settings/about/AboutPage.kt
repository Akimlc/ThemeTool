package xyz.akimlc.themetool.ui.page.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.BuildConfig
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar

@Composable
fun AboutPage(
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
) {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            BackTopAppBar(
                title = stringResource(R.string.about_title),
                scrollBehavior = topAppBarScrollBehavior,
                navController = navController
            )
        }
    ){ padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                BackgroundArea()
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Column {
                        SuperArrow(
                            title = "Akimlc",
                            summary = "Developer",
                            onClick = {
                                uriHandler.openUri("https://github.com/Akimlc")
                            },
                            leftAction = {
                                Image(
                                    painter = painterResource(R.mipmap.ic_akimlc),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(48.dp))
                                )
                            }
                        )
                        SuperArrow(
                            title = stringResource(R.string.about_thanks_list),
                            onClick = {
                                navController.navigate("ThanksPage")
                            }
                        )
                    }
                }
            }

            // 交流反馈
            item {
                SmallTitle(stringResource(R.string.title_feedback))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Column {
                        SuperArrow(
                            title = stringResource(R.string.group_qq),
                            onClick = {
                                uriHandler.openUri("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=1017168342&card_type=group&source=qrcode")
                            }
                        )
                        SuperArrow(
                            title = stringResource(R.string.channel_telegram),
                            onClick = { uriHandler.openUri("https://t.me/Theme_Tool") }
                        )
                        SuperArrow(
                            title = stringResource(R.string.group_telegram),
                            onClick = { uriHandler.openUri("https://t.me/ThemeToolChat") }
                        )
                    }
                }
            }

            item {
                SmallTitle(stringResource(R.string.title_other))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Column {
                        SuperArrow(
                            title = stringResource(R.string.donate),
                            summary = stringResource(R.string.donate_summary),
                            onClick = {
                                navController.navigate("DonationPage")
                            }
                        )
                        SuperArrow(
                            title = stringResource(R.string.reference),
                            onClick = {
                                navController.navigate("ReferencesPage")
                            }
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun BackgroundArea() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
            .padding(top = 8.dp)
            .padding(horizontal = 12.dp)
    ) {
        Image(
            painter = if (isSystemInDarkTheme())
                painterResource(R.mipmap.background_about_dark)
            else
                painterResource(R.mipmap.background_about_light),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)), // 设置12dp圆角,
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Theme Tool",
                fontSize = 28.sp,
                color = MiuixTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "版本号 ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}