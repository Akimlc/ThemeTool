package xyz.akimlc.themetool.ui.compoent

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.repository.Parse
import xyz.akimlc.themetool.viewmodel.DownloadViewModel
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.GlobalProductData
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.ProductData

@Composable
fun ThemeInfoDialog(
    isShow: MutableState<Boolean>,
    product: ProductData,
    downloadViewModel: DownloadViewModel
) {
    InfoDialog(
        isShow = isShow,
        title = stringResource(R.string.title_theme_info),
        nameLabelRes = R.string.label_theme_name,
        name = product.name,
        urlLabelRes = R.string.label_theme_url,
        parse = { Parse().parseTheme(product.uuid) },
        getUrl = { it?.themeUrl },
        downloadViewModel = downloadViewModel
    )
}


@Composable
fun GlobalThemeInfoDialog(
    isShow: MutableState<Boolean>,
    product: GlobalProductData,
    downloadViewModel: DownloadViewModel
) {
    InfoDialog(
        isShow,
        title = stringResource(R.string.title_theme_info),
        nameLabelRes = R.string.label_theme_name,
        name = product.name,
        urlLabelRes = R.string.label_theme_url,
        parse = { Parse().parseGlobalTheme(product.uuid) },
        getUrl = { it?.downloadUrl },
        downloadViewModel = downloadViewModel,
    )
}

@Composable
fun DomesticFontInfoDialog(
    isShow: MutableState<Boolean>,
    product: SearchFontViewModel.ProductData,
    downloadViewModel: DownloadViewModel
) {
    InfoDialog(
        isShow = isShow,
        title = stringResource(R.string.title_font_info),
        nameLabelRes = R.string.dialog_font_name,
        name = product.name,
        urlLabelRes = R.string.label_font_url,
        parse = { Parse().parseDomesticFont(product.uuid, product.name) },
        getUrl = {
            it?.downloadUrl
        },
        downloadViewModel = downloadViewModel
    )
}


@Composable
fun <T> InfoDialog(
    isShow: MutableState<Boolean>,
    title: String,
    nameLabelRes: Int,
    name: String,
    urlLabelRes: Int,
    parse: suspend () -> T?,
    getUrl: (T?) -> String?,
    context: Context = LocalContext.current,
    downloadViewModel: DownloadViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(true) }
    val parsedData = remember { mutableStateOf<T?>(null) }

    LaunchedEffect(isShow.value) {
        if (isShow.value && parsedData.value == null) {
            isLoading.value = true
            coroutineScope.launch {
                parsedData.value = parse()
                isLoading.value = false
            }
        }
    }

    SuperDialog(
        show = isShow,
        title = title,
        onDismissRequest = { isShow.value = false }
    ) {
        Column {
            Text(name)
            if (isLoading.value) {
                Text(stringResource(R.string.parsing))
            } else {
                val url = getUrl(parsedData.value) ?: stringResource(R.string.parse_failed)
                Text(stringResource(urlLabelRes, url))
            }

            Spacer(Modifier.height(12.dp))
            TextButton(
                text = stringResource(R.string.copy),
                onClick = {
                    val url = getUrl(parsedData.value)
                    if (url.isNullOrEmpty()) {
                        Toast.makeText(context, R.string.copy_failed, Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Theme URL", url)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(context, R.string.copy_success, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            TextButton(
                text = stringResource(R.string.download),
                onClick = {
                    val url = getUrl(parsedData.value)
                    if (url.isNullOrEmpty()) {
                        Toast.makeText(context, R.string.parse_failed, Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    downloadViewModel.fetchDownloadInfo(url, context)
                    Toast.makeText(context, R.string.added_to_download_list, Toast.LENGTH_SHORT)
                        .show()
                    isShow.value = false
                },
                colors = ButtonDefaults.textButtonColorsPrimary(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}