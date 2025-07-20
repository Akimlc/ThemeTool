package xyz.akimlc.themetool.ui.page


import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.page.download.DownloadPage
import xyz.akimlc.themetool.ui.page.settings.SettingsPage
import xyz.akimlc.themetool.viewmodel.DownloadViewModel

@OptIn(FlowPreview::class)
@Composable
fun MainPage(
    navController: NavController,
    downloadViewModel: DownloadViewModel
) {
    val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2
    )
    val pagerState = rememberPagerState(pageCount = { 3 })
    var targetPage by remember { mutableIntStateOf(pagerState.currentPage) }
    val coroutineScope = rememberCoroutineScope()
    val language = AppSettingsState.language.intValue
    val currentScrollBehavior = when (pagerState.currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        2 -> topAppBarScrollBehaviorList[2]
        else -> topAppBarScrollBehaviorList[0]
    }
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
    var pagerTitle by remember { mutableStateOf(items[targetPage].label) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
            targetPage = pagerState.currentPage
            pagerTitle = items[pagerState.currentPage].label
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = items.getOrNull(pagerState.currentPage)?.label ?: "",
                scrollBehavior = currentScrollBehavior
            )
        },
        bottomBar = {
            NavigationBar(
                items = items,
                selected = targetPage,
                onClick = { index ->
                    targetPage = index
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { padding ->

//        AppHorizontalPager(
//            navController = navController,
//            pagerState = pagerState,
//            topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
//            padding = padding,
//        )

        HorizontalPager(
            state = pagerState,
            pageContent = { page ->
                when (page) {
                    0 -> {
                        HomePage(
                            navController = navController,
                            topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                            padding = padding,
                        )
                    }

                    1 -> {
                        DownloadPage(
                            navController = navController,
                            topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
                            padding = padding,
                            viewModel = downloadViewModel
                        )
                    }

                    2 -> {
                        SettingsPage(
                            navController = navController,
                            topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                            padding = padding,
                        )
                    }
                }
            }
        )
    }
}