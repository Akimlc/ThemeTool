package xyz.akimlc.themetool.ui.page.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import xyz.akimlc.themetool.BuildConfig
import xyz.akimlc.themetool.R

@Composable
fun AboutPage(navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            TopAppBar(
                title = "关于",
                scrollBehavior = scrollBehavior,
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            topAppBarScrollBehavior = scrollBehavior,
            contentPadding = padding,
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // 你的应用图标
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Text(
                        text = "Theme Tool",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(BuildConfig.VERSION_NAME, fontSize = 14.sp, color = Color.Gray)
                }
            }

            item {
                SmallTitle("参与人员")
                Card(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
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
                    //感谢列表
                    SuperArrow(
                        title = "感谢列表",
                        onClick = {
                            navController.navigate("ThanksPage")
                        }
                    )
                }

            }

            item {
                SmallTitle("引用")
                Card(
                    modifier = Modifier.padding(horizontal = 12.dp)
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

