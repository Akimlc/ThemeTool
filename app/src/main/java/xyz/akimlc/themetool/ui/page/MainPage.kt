package xyz.akimlc.themetool.ui.page


import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.page.download.DownloadPage
import xyz.akimlc.themetool.ui.page.settings.SettingsPage

@OptIn(FlowPreview::class)
@Composable
fun MainPage(
    navController: NavController,
    pagerState: PagerState
) {
    val currentPage = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    val items = listOf(
        NavigationItem(
            stringResource(id = R.string.nav_home),
            ImageVector.vectorResource(R.drawable.ic_home)
        ),
        NavigationItem(
            stringResource(id = R.string.nav_download),
            ImageVector.vectorResource(R.drawable.ic_download)
        ),
        NavigationItem(
            stringResource(id = R.string.nav_settings),
            ImageVector.vectorResource(R.drawable.ic_settings)
        )
    )
    Scaffold(
        bottomBar = {
            NavigationBar(
                items = items,
                selected = currentPage,
                onClick = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            pageContent = { page ->
                when (page) {
                    0 -> {
                        HomePage(
                            navController = navController,
                        )
                    }

                    1 -> {
                        DownloadPage(
                            navController = navController,
                        )
                    }

                    2 -> {
                        SettingsPage(
                            navController = navController,
                        )
                    }
                }
            }
        )
    }
}