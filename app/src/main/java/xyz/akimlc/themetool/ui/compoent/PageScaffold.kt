package xyz.akimlc.themetool.ui.compoent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical


@Composable
fun PageScaffold(
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .overScrollVertical()
                .fillMaxSize()
        ) {
            content(innerPadding)
        }
    }
}

@Composable
fun SubPageScaffold(
    title: String,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            BackTopAppBar(
                title = title,
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .overScrollVertical()
                .fillMaxSize()
        ) {
            content(innerPadding)
        }
    }
}

@Composable
fun AppScaffold(
    title: String,
    navController: NavController? = null,
    content: LazyListScope.() -> Unit
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            if (navController == null) {
                TopAppBar(
                    title = title,
                    scrollBehavior = scrollBehavior
                )
            } else {
                BackTopAppBar(
                    title = title,
                    scrollBehavior = scrollBehavior,
                    navController = navController,
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .overScrollVertical()
                .fillMaxSize(),
            content = content
        )
    }
}