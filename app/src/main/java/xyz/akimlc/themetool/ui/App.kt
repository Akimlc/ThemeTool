package xyz.akimlc.themetool.ui

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.umeng.commonsdk.UMConfigure
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import xyz.akimlc.themetool.data.db.AppDatabase
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.page.HomePage
import xyz.akimlc.themetool.ui.page.MainPage
import xyz.akimlc.themetool.ui.page.PrivacyPage
import xyz.akimlc.themetool.ui.page.download.DownloadPage
import xyz.akimlc.themetool.ui.page.font.FontDetailPage
import xyz.akimlc.themetool.ui.page.font.FontSearchPage
import xyz.akimlc.themetool.ui.page.font.MtzFontPage
import xyz.akimlc.themetool.ui.page.font.ZipFontPage
import xyz.akimlc.themetool.ui.page.settings.FPSMonitor
import xyz.akimlc.themetool.ui.page.settings.about.AboutPage
import xyz.akimlc.themetool.ui.page.settings.about.DonationPage
import xyz.akimlc.themetool.ui.page.settings.about.ReferencesPage
import xyz.akimlc.themetool.ui.page.settings.about.ThanksPage
import xyz.akimlc.themetool.ui.page.theme.ThemeParsePage
import xyz.akimlc.themetool.ui.page.theme.ThemeSearchPage
import xyz.akimlc.themetool.utils.PreferenceUtil
import xyz.akimlc.themetool.viewmodel.DownloadViewModel
import xyz.akimlc.themetool.viewmodel.DownloadViewModelFactory
import xyz.akimlc.themetool.viewmodel.FontDetailViewModel
import xyz.akimlc.themetool.viewmodel.ParseViewModel
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun App() {

    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "download.db").build()
    }
    val dao = remember { db.downloadDao() }
    val downloadViewModel: DownloadViewModel = viewModel(
        factory = DownloadViewModelFactory(dao)
    )

    val showDialog = remember { mutableStateOf(!PreferenceUtil.isUserAgreed(context)) }
    val hasInit = remember { mutableStateOf(false) }
    val searchThemeViewModel: SearchThemeViewModel = viewModel()
    val parseViewModel: ParseViewModel = viewModel()
    LaunchedEffect(Unit) {
        AppSettingsState.showFPSMonitor.value =
            PreferenceUtil.getBoolean(context, "show_FPS_Monitor", false)
    }
    if (showDialog.value) {
        PrivacyPage(
            isShow = showDialog,
            onAgree = {
                PreferenceUtil.setUserAgreed(context, true)
                showDialog.value = false
            },
            onCancel = {
                (context as? Activity)?.finish()
            }
        )
        return
    } else if (!hasInit.value) {
        LaunchedEffect(Unit) {
            UMConfigure.init(
                context,
                "686a773c79267e0210a1d3db",
                "official",
                UMConfigure.DEVICE_TYPE_PHONE,
                null
            )
            hasInit.value = true
        }
    }
    val fontDetailViewModel: FontDetailViewModel = viewModel()
    val searchFontViewModel: SearchFontViewModel = viewModel()
    val navController = rememberNavController()
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val easing = FastOutSlowInEasing
    val paddingValues = PaddingValues(12.dp)
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = "MainPage",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            },
            exitTransition = {

                slideOutHorizontally(
                    targetOffsetX = { -it / 5 },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 5 },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            }
        ) {
            composable("MainPage") { MainPage(navController, downloadViewModel) }
            composable("HomePage") { HomePage(navController, scrollBehavior, paddingValues) }
            composable("ThemeSearchPage") { ThemeSearchPage(navController, searchThemeViewModel,downloadViewModel) }
            composable("ThemeParsePage") {
                ThemeParsePage(
                    navController,
                    parseViewModel,
                    downloadViewModel
                )
            }
            composable("ThanksPage") {
                ThanksPage(navController)
            }
            composable("ZipFontPage") {
                ZipFontPage(navController)
            }
            composable("FontSearchPage") {
                FontSearchPage(
                    searchFontViewModel,
                    fontDetailViewModel,
                    navController,
                    downloadViewModel
                )
            }
            composable("MtzFontPage") { MtzFontPage(navController) }
            composable("FontDetailPage/{uuid}") { backStackEntry ->
                val uuid = backStackEntry.arguments?.getString("uuid") ?: return@composable
                FontDetailPage(
                    navController = navController,
                    viewModel = fontDetailViewModel,
                    uuid = uuid,
                    downloadViewModel = downloadViewModel
                )
            }
            composable("DonationPage") {
                DonationPage(navController)
            }
            composable("ReferencesPage") {
                ReferencesPage(navController)
            }
            composable("DownloadPage") {
                DownloadPage(
                    navController,
                    scrollBehavior,
                    paddingValues,
                    viewModel = downloadViewModel
                )
            }
            composable("AboutPage") {
                AboutPage(navController, scrollBehavior)
            }
        }

        if (AppSettingsState.showFPSMonitor.value) {
            FPSMonitor(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 28.dp)
            )
        }
    }
}




