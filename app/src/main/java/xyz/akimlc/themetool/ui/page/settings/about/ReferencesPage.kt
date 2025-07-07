package xyz.akimlc.themetool.ui.page.settings.about


import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar

@Composable
fun ReferencesPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            BackTopAppBar(
                title = "引用",
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
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 6.dp)
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

