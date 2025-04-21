package xyz.akimlc.themetool.ui.page


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.HorizontalPager
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.page.about.AboutPage
import xyz.akimlc.themetool.ui.page.download.DownloadPage

@OptIn(FlowPreview::class)
@Composable
fun MainPage(navController: NavController) {
    val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2
    )
    val pagerState = rememberPagerState(pageCount = { 3 })
    var targetPage by remember { mutableIntStateOf(pagerState.currentPage) }
    val coroutineScope = rememberCoroutineScope()

    val currentScrollBehavior = when (pagerState.currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        else -> topAppBarScrollBehaviorList[2]
    }

    val items = listOf(
        NavigationItem("首页", ImageVector.vectorResource(R.drawable.ic_home)),
//        NavigationItem("下载", ImageVector.vectorResource(R.drawable.ic_download)),
        NavigationItem("关于", ImageVector.vectorResource(R.drawable.ic_about),)
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
                title = pagerTitle,
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
        HorizontalPager(
            pagerState = pagerState,
            pageContent = { page ->
                when (page) {
                    0 -> {
                        HomePage(
                            navController = navController,
                            topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                            padding = padding,
                        )
                    }

//                    1 -> {
//                        DownloadPage(
//                            navController = navController,
//                            topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
//                            padding = padding,
//                        )
//                    }

                    1 -> {
                        AboutPage(navController)
                    }
                }
            }
        )
    }
}