package xyz.akimlc.themetool.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import xyz.akimlc.themetool.ui.page.HomePage
import xyz.akimlc.themetool.ui.page.MainPage
import xyz.akimlc.themetool.ui.page.about.DonationPage
import xyz.akimlc.themetool.ui.page.about.ReferencesPage
import xyz.akimlc.themetool.ui.page.about.ThanksPage
import xyz.akimlc.themetool.ui.page.download.DownloadPage
import xyz.akimlc.themetool.ui.page.font.FontDetailPage
import xyz.akimlc.themetool.ui.page.font.FontSearchPage
import xyz.akimlc.themetool.ui.page.font.MtzFontPage
import xyz.akimlc.themetool.ui.page.font.ZipFontPage
import xyz.akimlc.themetool.ui.page.theme.ThemeParsePage
import xyz.akimlc.themetool.ui.page.theme.ThemeSearchPage
import xyz.akimlc.themetool.viewmodel.DownloadViewModel
import xyz.akimlc.themetool.viewmodel.FontDetailViewModel
import xyz.akimlc.themetool.viewmodel.ParseViewModel
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun App() {
    val fontDetailViewModel: FontDetailViewModel = viewModel()
    val searchFontViewModel: SearchFontViewModel = viewModel()
    val navController = rememberNavController()
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val easing = FastOutSlowInEasing
    val paddingValues = PaddingValues(12.dp)
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
        composable("MainPage") { MainPage(navController) }
        composable("HomePage") { HomePage(navController, scrollBehavior, paddingValues) }
        composable("ThemeSearchPage") { ThemeSearchPage(navController, SearchThemeViewModel()) }
        composable("ThemeParsePage") {
            ThemeParsePage(
                navController,
                ParseViewModel(),
                DownloadViewModel()
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
            )
        }
        composable("MtzFontPage") { MtzFontPage(navController) }
        composable("FontDetailPage/{uuid}") { backStackEntry ->
            val uuid = backStackEntry.arguments?.getString("uuid") ?: return@composable
            FontDetailPage(
                navController = navController,
                viewModel = fontDetailViewModel,
                uuid = uuid
            )
        }
        composable("DonationPage") {
            DonationPage(navController)
        }
        composable("ReferencesPage") {
            ReferencesPage(navController)
        }
        composable("DownloadPage") {
            DownloadPage(navController,scrollBehavior,paddingValues)
        }
    }
}




