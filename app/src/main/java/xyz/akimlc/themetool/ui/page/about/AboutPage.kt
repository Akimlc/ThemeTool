package xyz.akimlc.themetool.ui.page.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.BuildConfig
import xyz.akimlc.themetool.R

@Composable
fun AboutPage(
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = "关于",
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .padding(horizontal = 12.dp),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Theme Tool",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "版本号 ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            item {
                SmallTitle("参与人员")
                Card(modifier = Modifier.fillMaxWidth()) {
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
                            title = "感谢列表",
                            onClick = {
                                navController.navigate("ThanksPage")
                            }
                        )
                    }
                }
            }

            item {
                SmallTitle("交流 / 反馈")
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        SuperArrow(
                            title = "频道",
                            onClick = { uriHandler.openUri("https://t.me/Theme_Tool") }
                        )
                        SuperArrow(
                            title = "群组",
                            onClick = { uriHandler.openUri("https://t.me/ThemeToolChat") }
                        )
                    }
                }
            }

            item {
                SmallTitle("引用")
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    SuperArrow(
                        title = "MiuiX",
                        summary = "https://github.com/miuix-kotlin-multiplatform/miuix/",
                        onClick = {
                            uriHandler.openUri("https://github.com/miuix-kotlin-multiplatform/miuix/")
                        },
                    )
                    SuperArrow(
                        title = "OkHttp",
                        summary = "https://github.com/square/okhttp",
                        onClick = {
                            uriHandler.openUri("https://github.com/square/okhttp")
                        },
                    )
                    SuperArrow(
                        title = "Coil",
                        summary = "https://github.com/coil-kt/coil",
                        onClick = {
                            uriHandler.openUri("https://github.com/coil-kt/coil")
                        },
                    )
                    SuperArrow(
                        title = "Gson",
                        summary = "https://github.com/google/gson",
                        onClick = {
                            uriHandler.openUri("https://github.com/google/gson")
                        },
                    )
                }
            }
        }
    }
}

