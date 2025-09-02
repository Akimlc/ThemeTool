package xyz.akimlc.themetool.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.utils.getWindowSize
import xyz.akimlc.themetool.data.db.AppDatabase
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.page.HomePage
import xyz.akimlc.themetool.ui.page.MainPage
import xyz.akimlc.themetool.ui.page.download.DownloadPage
import xyz.akimlc.themetool.ui.page.font.FontDesignerPage
import xyz.akimlc.themetool.ui.page.font.FontDetailPage
import xyz.akimlc.themetool.ui.page.font.FontSearchPage
import xyz.akimlc.themetool.ui.page.font.MtzFontPage
import xyz.akimlc.themetool.ui.page.settings.FPSMonitor
import xyz.akimlc.themetool.ui.page.settings.SettingsPage
import xyz.akimlc.themetool.ui.page.settings.about.AboutPage
import xyz.akimlc.themetool.ui.page.settings.about.DonationPage
import xyz.akimlc.themetool.ui.page.settings.about.ReferencesPage
import xyz.akimlc.themetool.ui.page.settings.about.ThanksPage
import xyz.akimlc.themetool.ui.page.theme.ThemeParsePage
import xyz.akimlc.themetool.ui.page.theme.ThemeSearchPage
import xyz.akimlc.themetool.ui.page.welcome.WelcomePage
import xyz.akimlc.themetool.utils.PreferenceUtil
import xyz.akimlc.themetool.viewmodel.DownloadViewModel
import xyz.akimlc.themetool.viewmodel.DownloadViewModelFactory

@SuppressLint("ViewModelConstructorInComposable", "UnusedBoxWithConstraintsScope")
@Composable
fun App() {
    val context = LocalContext.current
    var firstLaunch = remember { mutableStateOf(true) }
    val welcomeState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "download.db").build()
    }
    val dao = remember { db.downloadDao() }
    val downloadViewModel: DownloadViewModel = viewModel(
        factory = DownloadViewModelFactory(dao)
    )
    LaunchedEffect(Unit) {
        Log.d("App", "App: 第一次启动: ${firstLaunch.value}")
        AppSettingsState.showFPSMonitor.value =
            PreferenceUtil.getBoolean("show_FPS_Monitor", false)
    }
    firstLaunch.value = !PreferenceUtil.getBoolean("first_launch", false)
    val parentRoute = remember { mutableStateOf("MainPage") }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val navController = rememberNavController()
    Scaffold { paddingValues ->
        when (firstLaunch.value) {
            true -> {
                WelcomePage(
                    firstLaunch,
                    pagerState = welcomeState
                )
            }

            false -> {
                MainLayout(navController, pagerState, parentRoute)
            }
        }
        AnimatedVisibility(
            AppSettingsState.showFPSMonitor.value
        ) {
            FPSMonitor(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 28.dp)
            )

        }
    }
}

/**
 * 小屏模式
 */
@Composable
fun MainLayout(
    navController: NavHostController,
    pagerState: PagerState,
    parentRoute: MutableState<String>
) {
    val easing = FastOutSlowInEasing
    val windowWidth = getWindowSize().width
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "download.db").build()
    }
    val dao = remember { db.downloadDao() }
    val downloadViewModel: DownloadViewModel = viewModel(
        factory = DownloadViewModelFactory(dao)
    )
    NavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = Route.MAIN,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { windowWidth },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -windowWidth / 5 },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -windowWidth / 5 },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { windowWidth },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        },
        builder = {
            composable(
                Route.MAIN
            ) {
                MainPage(navController,pagerState)
            }
            pagerContent(
                navController,
                parentRoute
            )
        }
    )
}

fun NavGraphBuilder.pagerContent(
    navController: NavHostController,
    parentRoute: MutableState<String>
) {
    composable(Route.HOME) {
        HomePage(navController)
    }
    composable(Route.SETTINGS) {
        SettingsPage(navController)
    }
    composable(Route.ABOUT) {
        AboutPage(navController)
    }
    composable(Route.DOWNLOAD) {
        DownloadPage(navController)
    }
    composable(FontPageList.SEARCH) {
        FontSearchPage(navController)
    }
    composable(FontPageList.DETAIL) { backStackEntry ->
        val uuid = backStackEntry.arguments?.getString("uuid") ?: return@composable
        FontDetailPage(
            navController = navController,
            uuid = uuid
        )
    }
    composable(FontPageList.MTZ) {
        MtzFontPage(navController)
    }
    composable(ThemePageList.SEARCH) {
        ThemeSearchPage(navController)
    }
    composable(ThemePageList.PARSE) {
        ThemeParsePage(navController)
    }
    composable(AboutPageList.THANKS) {
        ThanksPage(navController)
    }
    composable(AboutPageList.REFERENCES) {
        ReferencesPage(navController)
    }
    composable(AboutPageList.DONATION) {
        DonationPage(navController)
    }
    composable(
        route = FontPageList.DESIGNER
    ) { backStackEntry ->
        val designerId = backStackEntry.arguments?.getString("designerId") ?: ""
        FontDesignerPage(navController, designerId)
    }
}



