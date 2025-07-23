package xyz.akimlc.themetool.ui.page


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Room
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.VerticalDivider
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.data.db.AppDatabase
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.page.download.DownloadPage
import xyz.akimlc.themetool.ui.page.settings.SettingsPage
import xyz.akimlc.themetool.viewmodel.DownloadViewModel
import xyz.akimlc.themetool.viewmodel.DownloadViewModelFactory

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

//    LaunchedEffect(pagerState) {
//        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
//            targetPage = pagerState.currentPage
//            pagerTitle = items[pagerState.currentPage].label
//        }
//    }
    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = items.getOrNull(pagerState.currentPage)?.label ?: "",
//                scrollBehavior = currentScrollBehavior
//            )
//        },
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


@Composable
fun LandscapeMainPage(
    navController: NavHostController,
    pagerState: PagerState,
) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "download.db").build()
    }
    val dao = remember { db.downloadDao() }

    val downloadViewModel: DownloadViewModel = viewModel(
        factory = DownloadViewModelFactory(dao)
    )
    val coroutineScope = rememberCoroutineScope()
    val dividerLineColor = colorScheme.dividerLine
    val selectedPage = pagerState.currentPage
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

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        NavigationBarForStart(
            modifier = Modifier.weight(0.25f).widthIn(max = 130.dp),
            items = items,
            selected = selectedPage,
            onClick = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }

        )
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 12.dp)
                .width(0.75.dp),
            color = colorScheme.dividerLine
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .widthIn(min = 400.dp),
            contentPadding = PaddingValues(0.dp),
            beyondViewportPageCount = 3,
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

@Composable
fun NavigationBarForStart(
    items: List<NavigationItem>,
    selected: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier,
        popupHost= { },
    ) {
        LazyColumn(
            modifier = Modifier.height(getWindowSize().height.dp),
            userScrollEnabled = false,
            contentPadding = PaddingValues(
                top = it.calculateTopPadding() + 13.dp,
                bottom = it.calculateBottomPadding()
            ),
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selected==index
                item {

                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            Color.Black.copy(alpha = 0.1f)
                        } else {
                            Color.Transparent
                        }, label = ""
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        BoxWithConstraints(
                            Modifier
                                .padding(bottom = 5.dp)
                                .background(bgColor, SmoothRoundedCornerShape(8.dp, 0.5f))
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            onClick.invoke(index)
                                        }
                                    )
                                }
                        ){
                            val maxWidth = this.maxWidth
                            Row(
                                Modifier.padding(horizontal = if(maxWidth > 90.dp) 16.dp else 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp),
                                    tint = colorScheme.onBackground
                                )
                                if(maxWidth > 90.dp){
                                    Text(
                                        text = item.label,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp),
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        fontWeight = FontWeight(550)

                                    )

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}