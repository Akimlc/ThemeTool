package xyz.akimlc.themetool.ui.page.settings.about


import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar

data class ReferenceItem(val name: String, val url: String)
@Composable
fun ReferencesPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val references = listOf(
        ReferenceItem("MiuiX", "https://github.com/miuix-kotlin-multiplatform/miuix/"),
        ReferenceItem("OkHttp", "https://github.com/square/okhttp"),
        ReferenceItem("Coil", "https://github.com/coil-kt/coil"),
        ReferenceItem("Gson", "https://github.com/google/gson"),
    )
    Scaffold(
        topBar = {
            BackTopAppBar(
                title = stringResource(R.string.title_references),
                scrollBehavior = scrollBehavior,
                navController = navController,
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxHeight()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                ReferenceCard(references = references)
            }
        }
    }
}


@Composable
fun ReferenceCard(references: List<ReferenceItem>) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 6.dp, bottom = 16.dp)
    ) {
        references.forEach { item ->
            SuperArrow(
                title = item.name,
                summary = item.url,
                onClick = {
                    uriHandler.openUri(item.url)
                }
            )
        }
    }
}
